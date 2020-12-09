package com.chuang.tauceti.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.chuang.tauceti.generator.*;
import com.chuang.tauceti.generator.INameConvert;
import com.chuang.tauceti.generator.initializer.ContextInitializer;
import com.chuang.tauceti.generator.initializer.ContextInitializers;
import com.chuang.tauceti.generator.initializer.DefaultContextInitializer;
import com.chuang.tauceti.tools.basic.StringKit;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import com.chuang.tauceti.tools.basic.collection.DoubleKeyMap;
import com.chuang.tauceti.tools.basic.collection.UnmodifiableDoubleKeyMap;
import com.chuang.tauceti.tools.basic.reflect.ClassSearch;
import com.chuang.urras.rowquery.IRowQueryService;
import com.chuang.urras.rowquery.RowQueryService;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 为了把注意力集中在必要的配置上。 GenConfig 会尽可能的减少配置。减少的配置如下：
 * 1，MP默认关闭的配置都去掉
 * 2，我们会默认选择与脚手架本身更契合的配置，比如 MP默认不开启swagger2配置。但由于Anr脚手架默认支持swagger2，因此默认会开启这个配置。
 * 3，我们会尽量选择更契合 java 1.8+ 的配置，如日期类型为 DateType.TIME_PACK
 * 如果你仍然希望修改 MP的配置可以在build的时候单独配置。
 *
 * 关于渲染参数：
 * mp 模板生成的参数几乎全部来自于 {@link AbstractTemplateEngine#getObjectMap(TableInfo)}
 * 同时，我们可以在每个表格生成前给表格提供更多的 模板参数
 *
 *
 *  InjectionConfig 作用步骤
 *  1, 调用 {@link InjectionConfig#initMap()} 针对全局所有表提供参数，initMap的目的是初始化内部 map 对象, 此方法只调用一次
 *  2, 遍历所有 TableInfo，为每个TableInfo生成 objectMap，生成后会调用 {@link InjectionConfig#prepareObjectMap} 来为 objectMap做预处理
 *  3，遍历所有 TableInfo 调用 {@link InjectionConfig#initTableMap(TableInfo)} 继续初始化内部 map 对象，每个tableInfo 调用一次
 *  4, objectMap处理完成后，会获取 initMap()方法初始化的 map的对象，并以 cfg为key存进去。因此 initMap存入的全局参数都可以在模板中以 cfg.xxx来使用
 *  5, 输出模板
 * 综上，虽然每个 TableInfo 都会创建一个objectMap，但因为MP只会通过 InjectionConfig.getMap来获取参数。且 InjectionConfig是独一份的。
 * 因此无论在 initMap还是 initTableMap中初始化,自定义参数都只能放在同一个InjectionConfig的map对象中。
 * 这意味着，initTableMap中创建的变量并不是特定 table独享。每个table创建后放入map的对象都会被后来的table所使用。
 *
 * GenConfig 将尝试重写 InjectionConfig, 让 initMap 的参数是全局的，而 initTableMap的参数是每个表独享的，避免可能造成的冲突。
 * 为避免误操作，GenConfig不提供直接对InjectionConfig的操作，而是通过 GenConfig 来提供相关的接口。
 *
 * GenConfig 还将提供更细粒度的参数, 详细说明请查看 {@link ContextInitializer}
 *
 * @see ContextInitializer
 * @see Generator
 *
 */
@Getter
public class GenConfig {
    /**
     * 全局配置
     */
    private final GlobalConfig global;
    /**
     * 数据源配置
     */
    private final DataSourceConfig dataSource;
    /**
     * 策略配置
     */
    private final StrategyConfig strategy;

    private final TemplateConfig template;

    private final String rootPackage;

    private final AbstractTemplateEngine templateEngine;

    private final TauCetiInjectionConfig injection;

    private final INameConvert nameConvert;

    private final boolean mvn;
    private final List<Generator> generators;


    private final UnmodifiableDoubleKeyMap<String, String, Class<? extends Enum<?>>> enums;


    public GenConfig(boolean mvn,
                     boolean debug,
                     INameConvert nameConvert,
                     GlobalConfig global,
                     DataSourceConfig dataSource,
                     StrategyConfig strategy,
                     String rootPackage,
                     List<Generator> generators,
                     AbstractTemplateEngine templateEngine,
                     UnmodifiableDoubleKeyMap<String, String, Class<? extends Enum<?>>> enums,
                     ContextInitializer initializer) {
        this.mvn = mvn;
        this.global = global;
        this.nameConvert = nameConvert;
        this.dataSource = dataSource;
        this.strategy = strategy;
        this.rootPackage = rootPackage;
        this.templateEngine = templateEngine;
        this.enums = enums;

        // ========= 模板 =========
        // 取消所有 mybatis plus 生成的内容
        this.template = new TemplateConfig();
        template.setController(null);
        template.setXml(null);
        template.setEntity(null);
        template.setEntityKt(null);
        template.setService(null);
        template.setServiceImpl(null);
        this.generators = generators;

        if(isMvn()) {
            List<MvnWrapper> wrappers = new ArrayList<>();
            for (Generator gen : generators) {
                if(gen instanceof MvnWrapper) {
                    wrappers.add((MvnWrapper) gen);
                } else {
                    wrappers.add(new MvnWrapper(gen, null));
                }
            }
            generators.clear();
            generators.addAll(wrappers);
            generators.sort(Generator::compareTo);
        }

        ContextInitializers init = new ContextInitializers();
        init.add(new DefaultContextInitializer());
        if(null != initializer) {
            init.add(initializer);
        }
        this.injection = new TauCetiInjectionConfig(debug, this, init);

    }

    public void gen() {
        new Coder().gen(this);
    }

    public static Builder create() {
        return new Builder();
    }


    public static class Builder {
        private String author;
        private String jdbcUrl;
        private String jdbcDriver;
        private String jdbcUsername;
        private String jdbcPassword;
        private String[] includeTables = new String[0];
        private String rootPackage;
        private String[] tablePrefix;
        private boolean mvn = true;
        private boolean debug = true;

        private final Map<GenType, String> typeNameMap = new HashMap<>();

        private INameConvert nameConvert = genType -> {
            if(genType == GenType.SERVICE || genType.name().equals(GenType.SERVICE.name())) {
                return "I%sService";
            } else {
                return "%s" + StringKit.firstCharToUpperCase(genType.name());
            }
        };

        private final List<Generator> generators = new ArrayList<>();

        private AbstractTemplateEngine templateEngine;

        private Consumer<GlobalConfig> global;
        private Consumer<StrategyConfig> strategy;

        private ContextInitializer initializer;


        private final DoubleKeyMap<String, String, Class<? extends Enum<?>>> enums = new DoubleKeyMap<>();

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder templateEngine(AbstractTemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
            return this;
        }

        public Builder mvn(boolean mvn) {
            this.mvn = mvn;
            return this;
        }


        public Builder mvn() {
            return this.mvn(true);
        }

        public void gen() {
            build().gen();
        }

        public Builder enums(String table, String field, Class<? extends Enum<?>> enumClass) {
            this.enums.put(table, field, enumClass);
            return this;
        }

        public Builder debug() {
            return this.debug(true);
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder nameConvert(INameConvert nameConvert) {
            this.nameConvert = nameConvert;
            return this;
        }

        public Builder nameConvert(GenType type, String name) {
            this.typeNameMap.put(type, name);
            return this;
        }

        public Builder enums(Class<? extends Enum<?>> enumClass, String... tableWithField) {
            for (String tf : tableWithField) {
                String[] tableAndField = tf.split(":");
                enums(tableAndField[0], tableAndField[1], enumClass);
            }
            return this;
        }

        public Builder tablePrefix(String... tablePrefix) {
            this.tablePrefix = tablePrefix;
            return this;
        }

        public Builder rootPackage(String rootPackage) {
            this.rootPackage = rootPackage;
            return this;
        }

        public Builder jdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
            return this;
        }
        public Builder jdbcDriver(String jdbcDriver) {
            this.jdbcDriver = jdbcDriver;
            return this;
        }
        public Builder jdbcUsername(String jdbcUsername) {
            this.jdbcUsername = jdbcUsername;
            return this;
        }
        public Builder jdbcPassword(String jdbcPassword) {
            this.jdbcPassword = jdbcPassword;
            return this;
        }
        public Builder includeTables(String... includeTables) {
            this.includeTables = CollectionKit.addAll(this.includeTables, includeTables);
            return this;
        }

        public Builder global(Consumer<GlobalConfig> global) {
            this.global = global;
            return this;
        }

        public Builder strategy(Consumer<StrategyConfig> strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder initializer(ContextInitializer initializer) {
            this.initializer = initializer;
            return this;
        }

        public Builder lookup() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
            return this.lookup("com.chuang.tauceti.generator.impl", Generator.class);
        }

        public Builder lookup(String rootPackage, Class<? extends Generator> parent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            Collection<Class<?>> classes = ClassSearch.findClass(rootPackage, true, ClassSearch.and(parent));
            for (Class<?> aClass : classes) {
                Generator generator = (Generator) aClass.newInstance();
                addImpl(generator);
            }
            return this;
        }

        private void addImpl(Generator generator) {
            Set<Generator> set =
                    generators.stream().filter(gen-> gen.type().equals(generator.type())).collect(Collectors.toSet());
            generators.removeAll(set);
            generators.add(generator);
        }

        public GenConfig build() {
            INameConvert nameConvert = type-> typeNameMap.getOrDefault(type, this.nameConvert.className(type));
            // ========= 全局配置 =========
            GlobalConfig global = new GlobalConfig();
            global.setOpen(false);
            global.setOutputDir(System.getProperty("user.dir"));
            global.setAuthor(author);
            global.setIdType(IdType.AUTO);                                              // 默认系统较小，使用自增id
            global.setFileOverride(false);                                              // 不允许覆盖文件，避免将原来已经代码覆盖
            global.setSwagger2(false);                                                  // 默认实体类不生成swagger, 我们直在接口层的model加swagger
            global.setServiceName(nameConvert.className(GenType.SERVICE));             // service 接口名
            global.setServiceImplName(nameConvert.className(GenType.SERVICE_IMPL));    // service 实现类名
            global.setControllerName(nameConvert.className(GenType.CONTROLLER));       // controller 类名
            global.setEntityName(nameConvert.className(GenType.ENTITY));               // entity 类名
            global.setMapperName(nameConvert.className(GenType.MAPPER));               // mapper 类名
            if(null != this.global) {
                this.global.accept(global);
            }


            // ========= 数据源配置 =========
            DataSourceConfig dataSource = new DataSourceConfig();
            if (jdbcUrl.contains("oracle")) {
                dataSource.setDbType(DbType.ORACLE);
            } else if (jdbcUrl.contains("postgresql")) {
                dataSource.setDbType(DbType.POSTGRE_SQL);
            } else if (jdbcUrl.contains("sqlserver")) {
                dataSource.setDbType(DbType.SQL_SERVER);
            } else {
                dataSource.setDbType(DbType.MYSQL);
            }
            dataSource.setDriverName(jdbcDriver);
            dataSource.setUrl(jdbcUrl);
            dataSource.setUsername(jdbcUsername);
            dataSource.setPassword(jdbcPassword);

            // ========= 策略配置 =========
            StrategyConfig strategy = new StrategyConfig();
            strategy.setTablePrefix(tablePrefix);                           // 移除表前缀
            strategy.setNaming(NamingStrategy.underline_to_camel);          // 表名生成策略
            strategy.setColumnNaming(NamingStrategy.underline_to_camel);    // 列名生成策略
            strategy.setEntityTableFieldAnnotationEnable(true);             // 是否添加注释
            strategy.setControllerMappingHyphenStyle(true);                 // controller request路径使用驼峰
            strategy.setInclude(includeTables);
            strategy.setEntityLombokModel(true);
            strategy.setSuperServiceClass(IRowQueryService.class.getName());
            strategy.setSuperServiceImplClass(RowQueryService.class.getName());
            strategy.setRestControllerStyle(true);
            strategy.setLogicDeleteFieldName("deleted");
            strategy.setTableFillList(Arrays.asList(
                    new TableFill("created_time", FieldFill.INSERT),
                    new TableFill("creator", FieldFill.INSERT),
                    new TableFill("updated_time", FieldFill.UPDATE),
                    new TableFill("updater", FieldFill.UPDATE)
            ));
            if(null != this.strategy) { this.strategy.accept(strategy); }
            strategy.setNameConvert(null);

            generators.sort(Generator::compareTo);
            return new GenConfig(
                    mvn,
                    debug,
                    nameConvert,
                    global,
                    dataSource,
                    strategy,
                    rootPackage,
                    generators,
                    templateEngine,
                    enums.toUnmodifiable(),
                    initializer);

        }
    }
}
