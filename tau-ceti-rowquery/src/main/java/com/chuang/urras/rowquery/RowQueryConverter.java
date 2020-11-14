package com.chuang.urras.rowquery;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chuang.tauceti.tools.basic.IOKit;
import com.chuang.urras.rowquery.filters.*;
import com.sun.istack.internal.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Created by ath on 2017/5/29.
 */
public class RowQueryConverter extends AbstractHttpMessageConverter<RowQuery> {

    //自定义媒体类型
    public RowQueryConverter(){
        super(new MediaType("application", "row-query", Charset.forName("UTF-8")));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return RowQuery.class.isAssignableFrom(clazz);
    }

    @Override
    protected RowQuery readInternal(Class<? extends RowQuery> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        String str = IOKit.read(inputMessage.getBody(), "UTF-8");
        JSONObject json = JSONObject.parseObject(str);


        RowQuery rq = new RowQuery();

        rq.setPageSize(json.getInteger("pageSize"));
        rq.setPageNum(json.getInteger("startRow") / rq.getPageSize() + 1);


        if(json.containsKey("sorts")) {
            JSONArray sorts = json.getJSONArray("sorts");
            rq.setSorts(new RowQuery.SortModel[sorts.size()]);
            for(int i = 0; i < sorts.size(); i++) {
                rq.getSorts()[i] = sorts.getObject(i, RowQuery.SortModel.class);
            }
        }

        if(json.containsKey("filters")) {
            JSONArray filters = json.getJSONArray("filters");
            rq.setFilters(new RowQuery.Filter[filters.size()]);
            for(int i = 0; i < filters.size(); i++) {
                String filterType = filters.getJSONObject(i).getString("filterType");
                Optional<Class<? extends RowQuery.Filter>> c = getFilterClass(filterType);
                if(!c.isPresent()) {
                    logger.warn(filterType + "类型不支持");
                    continue;
                }
                rq.getFilters()[i] = filters.getObject(i, c.get());
            }
        }



        return rq;
    }

    protected Optional<Class<? extends RowQuery.Filter>> getFilterClass(String filterType) {
        if("text".equalsIgnoreCase(filterType)) {
            return Optional.of(TextFilter.class);
        }
        if("set".equalsIgnoreCase(filterType)) {
            return Optional.of(SetFilter.class);
        }
        if("number".equalsIgnoreCase(filterType)) {
            return Optional.of(NumberFilter.class);
        }
        if("Date".equalsIgnoreCase(filterType)) {
            return Optional.of(DateFilter.class);
        }
        return Optional.empty();
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }
    @Override
    protected void writeInternal(RowQuery rowQuery, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        throw new HttpMessageNotWritableException("not support row-query writer out");
    }


}
