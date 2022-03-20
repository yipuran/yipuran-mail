package org.yipuran.mail.pop;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Names;

/**
 * メール受信 PopAgentインスタンス生成ビルダー.
 * <pre>
 * メール受信を実装するPopAgentインスタンスを生成する。
 * 受信ハンドラ PopHandler インターフェース実装を約束させることでメールの受信、
 * ＰＯＰ３の処理を隠蔽し受信したメールの処理をハンドラに記述させることを
 * 目的としている。
 *
 *   // 受信メールAgent 生成
 *   PopAgent agent = PopAgentBuilder.create(host, user, passwd, handlerclass);
 *
 *   // メール受信
 *   agent.recv();
 *
 *   PopAgent生成の引数
 *   String host     = ＰＯＰ３ホスト名または、ＩＰアドレス
 *   String user     = メールアカウント
 *   String passwd   = パスワード
 *   Class<? extends PopHandler> handlerclass =  PopHandlerを実装するクラス
 * </pre>
 * @since 1.1.6
 */
public final class PopAgentBuilder{
	private PopAgentBuilder(){}
	/**
	 * PopAgentインスタンスを生成.
	 * @param host ＰＯＰ３ホスト名または、ＩＰアドレス
	 * @param user メールアカウント
	 * @param passwd パスワード
	 * @param handlerclass PopHandlerを実装するクラス<br>引数なしデフォルトコンストラクタで生成される。
	 * @return PopAgentインスタンス
	 */
	public static PopAgent create(final String host, final String user, final String passwd, final Class<? extends PopHandler> handlerclass){
		Injector injector = Guice.createInjector(new AbstractModule(){
			@Override
			protected void configure(){
				binder().bind(String.class).annotatedWith(Names.named("HOST")).toInstance(host);
				binder().bind(String.class).annotatedWith(Names.named("USER")).toInstance(user);
				binder().bind(String.class).annotatedWith(Names.named("PASSWORD")).toInstance(passwd);
				binder().bind(PopHandler.class).to(handlerclass);
			}
		});
		return injector.getInstance(PopAgentImpl.class);
	}
	/**
	 * PopAgentインスタンスを生成（PopHandlerプロバイダ指定）.
	 * <pre>
	 * PopHandlerを実装するクラスが、引数なしデフォルトコンストラクタで生成できない時は、
	 * このメソッドで、PopHandlerを実装するインスタンス生成プロバイダを指定する。
	 * </pre>
	 * @param host ＰＯＰ３ホスト名または、ＩＰアドレス
	 * @param user メールアカウント
	 * @param passwd パスワード
	 * @param provider PopHandlerを実装するインスタンス生成プロバイダ
	 * @return PopAgentインスタンス
	 */
	public static PopAgent createByProvider(final String host, final String user, final String passwd, final Class<? extends Provider<PopHandler>> provider){
		Injector injector = Guice.createInjector(new AbstractModule(){
			@Override
			protected void configure(){
				binder().bind(String.class).annotatedWith(Names.named("HOST")).toInstance(host);
				binder().bind(String.class).annotatedWith(Names.named("USER")).toInstance(user);
				binder().bind(String.class).annotatedWith(Names.named("PASSWORD")).toInstance(passwd);
				binder().bind(PopHandler.class).toProvider(provider);
			}}
		);
		return injector.getInstance(PopAgentImpl.class);
	}
}
