package com.chuang.tauceti.sdk.payment;

import com.chuang.tauceti.sdk.payment.deposit.DepositCallbackInfo;
import com.chuang.tauceti.sdk.payment.deposit.DepositInfo;
import com.chuang.tauceti.sdk.payment.deposit.DepositRequest;
import com.chuang.tauceti.sdk.payment.query.QueryInfo;
import com.chuang.tauceti.sdk.payment.query.QueryRequest;
import com.chuang.tauceti.sdk.payment.withdraw.WithdrawCallbackInfo;
import com.chuang.tauceti.sdk.payment.withdraw.WithdrawInfo;
import com.chuang.tauceti.sdk.payment.withdraw.WithdrawRequest;
import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.enums.Bank;
import com.chuang.tauceti.support.enums.PaymentType;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.tools.basic.reflect.ClassSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PaymentSDK {
    private final Logger logger = LoggerFactory.getLogger(PaymentSDK.class);

    /**
     * 配置加载策略，该接口提供了一个抽象的配置来源。由使用sdk的模块来提供实现。
     * 这样做的目的是为了对业务进行解耦，避免sdk依赖其他的业务实现。
     */
    private final ConfigLoadPolicy configPolicy;
    /**
     * 所有的平台对象缓存，在启动时统一加载。平台对象为单例，这里默认所有的平台对象实现都是线程安全的。
     */
    private final Map<String, IPaymentPlatform> platforms = new HashMap<>();

    public PaymentSDK(ConfigLoadPolicy configPolicy) {
        this(configPolicy, "com.chuang.payment.sdk.impl");
    }

    /**
     * 根据配置加载策略和实现类包集合来创建sdk。创建流程如下：
     * 创建时会扫描 scanImplPackage 包下的所有类，如果它们实现了{@link IPaymentPlatform} 以及使用 @{@link Platform}进行注解。那么该对象将作为一个平台实现被加载到缓存中。
     *
     */
    public PaymentSDK(ConfigLoadPolicy configPolicy, String... scanImplPackage) {
        this.configPolicy = configPolicy;
        if(scanImplPackage == null) {
            return;
        }
        for(String pack: scanImplPackage) {
            loadImpl(pack);
        }
    }

    private void loadImpl(String pack) {
        try {
            Collection<Class<?>> list = ClassSearch.findClass(pack, true, ClassSearch.and(IPaymentPlatform.class, Platform.class));
            for (Class<?> clazz : list) {
                Platform ann = clazz.getAnnotationsByType(Platform.class)[0];
                String platform = ann.value();
                platforms.put(platform, (IPaymentPlatform) clazz.newInstance());
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.error("load payment impl error, the base package is " + pack, e);
        }
    }

    /**
     * 根据request 获取 配置
     */
    public Optional<PaymentPlatformConfig> loadConfig(PaymentRequest request) {
        return this.configPolicy.loadConfig(request, this);
    }

    /**
     * 根据 key 来加载配置，加载方式是使用 configPolicy 的策略进行的。
     */
    public Optional<PaymentPlatformConfig> loadConfigByKey(String key) {
        return this.configPolicy.loadConfig(key);
    }

    /**
     * 发起一次请求，自动加载配置
     */
    public CompletableFuture<Result<DepositInfo>> deposit(DepositRequest request) {
        Optional<PaymentPlatformConfig> config = this.configPolicy.loadConfig(request, this);
        if(config.isPresent()) {
            throw new BusinessException(Result.FAIL_CODE, "没有可用通道");
        }

        IPaymentPlatform platform = platforms.get(config.get().getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.get().getPlatform() + " 平台");
        }
        return deposit(request, config.get());
    }

    /**\
     * 发起一次取款，自动加载配置
     */
    public CompletableFuture<Result<WithdrawInfo>> withdraw(WithdrawRequest request) {
        Optional<PaymentPlatformConfig> config = this.configPolicy.loadConfig(request, this);
        if(config.isPresent()) {
            throw new BusinessException(Result.FAIL_CODE, "没有可用通道");
        }

        IPaymentPlatform platform = platforms.get(config.get().getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.get().getPlatform() + " 平台");
        }
        return withdraw(request, config.get());
    }

    /**
     * 根据request和config发起一次存款
     * @param request
     * @param config
     * @return
     */
    public CompletableFuture<Result<DepositInfo>> deposit(DepositRequest request, PaymentPlatformConfig config) {
        IPaymentPlatform platform = platforms.get(config.getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.getPlatform() + " 平台");
        }
        return platform.deposit(request, config);
    }

    public IPaymentPlatform getPlatform(String platformCode) {
        return platforms.get(platformCode);
    }

    /**
     * 根据request和config发起一次取款
     */
    public CompletableFuture<Result<WithdrawInfo>> withdraw(WithdrawRequest request, PaymentPlatformConfig config) {
        IPaymentPlatform platform = platforms.get(config.getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.getPlatform() + " 平台");
        }
        return platform.withdraw(request, config);
    }

    public Result<DepositCallbackInfo> depositCallbackHand(Map<String, String> params, String body, boolean isFront, String configKey) {
        Optional<PaymentPlatformConfig> config = this.configPolicy.loadConfig(configKey);
        if(!config.isPresent()) {
            throw new BusinessException(Result.FAIL_CODE, "无法找到配置，key->" + configKey);
        }
        IPaymentPlatform platform = platforms.get(config.get().getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.get().getPlatform() + " 平台");
        }
        return platform.depositCallback(params, body, isFront, config.get());
    }

    public Result<DepositCallbackInfo> depositCallbackHand(Map<String, String> params, String body, boolean isFront, PaymentPlatformConfig config) {
        IPaymentPlatform platform = platforms.get(config.getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.getPlatform() + " 平台");
        }
        return platform.depositCallback(params, body, isFront, config);
    }

    public Result<WithdrawCallbackInfo> withdrawCallbackHand(Map<String, String> params, String body, String configKey) {
        Optional<PaymentPlatformConfig> config = this.configPolicy.loadConfig(configKey);
        if(!config.isPresent()) {
            throw new BusinessException(Result.FAIL_CODE, "无法找到配置，key->" + configKey);
        }
        IPaymentPlatform platform = platforms.get(config.get().getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.get().getPlatform() + " 平台");
        }
        return platform.withdrawCallback(params, body, config.get());
    }

    public Result<WithdrawCallbackInfo> withdrawCallbackHand(Map<String, String> params, String body, PaymentPlatformConfig config) {
        IPaymentPlatform platform = platforms.get(config.getPlatform());
        if(null == platform) {
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.getPlatform() + " 平台");
        }
        return platform.withdrawCallback(params, body, config);
    }

    /**
     * 判断是否支持 特定平台，特定支付类型，以及特定的银行编号。
     * @param platformCode
     * @param type
     * @param bank
     * @return
     */
    public boolean supportBank(String platformCode, PaymentType type, Bank bank) {
        IPaymentPlatform platform = platforms.get(platformCode);
        if(null == platform) {
            logger.warn("can not find " + platformCode + ", all platform codes are " + platforms);
            return false;
        }

        return platform.isSupport(type, bank);
    }

    public CompletableFuture<Result<QueryInfo>> query(QueryRequest info, PaymentPlatformConfig config) {
        IPaymentPlatform platform = platforms.get(config.getPlatform());
        if(null == platform) {
            logger.warn("can not find " + config.getPlatform() + ", all platform codes are " + platforms);
            throw new BusinessException(Result.FAIL_CODE, "sdk 不支持 " + config.getPlatform() + " 平台");
        }

        return platform.query(info, config);
    }


}
