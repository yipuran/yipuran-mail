/**
 * メール送信パッケージ.
 * <PRE>
 * Properties ファイルで記述する SMTP 設定ファイルを配置して、スレッドによるメール送信タスクを提供する。
 *
 * Injector injector = Guice.createInjector(new AbstractModule(){
 *    &#064;Override
 *    protected void configure(){
 *       binder().bind(String.class).annotatedWith(Names.named("MAIL_PROPERTIES_PATH")).toInstance(path);
 *    }
 * });
 *
 * SendMailTaskBuilder builder = injector.getInstance(SendMailTaskBuilder.class);
 * SendmailTask task = builder.create("xxx@xxxx", "xxx@xxx", "xxxx@xxxx", "テスト件名", "メッセージ");
 * ExecutorService service = Executors.newSingleThreadExecutor();
 * Future<Integer> future =  service.submit(task);
 * try{
 *    Integer sts = future.get();
 *    System.out.println(sts);
 * } catch (InterruptedException e) {
 *   e.printStackTrace();
 * } catch (ExecutionException e) {
 *    e.printStackTrace();
 * }finally{
 *    service.shutdown();
 * }
 *
 * </PRE>
 */
package org.yipuran.mail;

