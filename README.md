# tau-ceti
# 坑
* shiro filter 的创建一定不要用 @Bean 声音，因为这样同时也会被当作一个普通的filter使用。普通的filter不管路径是否匹配，每次都会有效果。
* 注意，使用jwt时，如果访问的请求路径不在 jwt 过滤器中，无论用户是否登录，是否有jwt token，都无法获取ShiroUser，因为ShiroUser是通过jwt filter来生成的。没有经过这个filter是不行的。
