# tau-ceti
# 坑
* shiro filter 的创建一定不要用 @Bean 声音，因为这样同时也会被当作一个普通的filter使用。普通的filter不管路径是否匹配，每次都会有效果。
