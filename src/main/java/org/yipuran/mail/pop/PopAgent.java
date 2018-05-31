package org.yipuran.mail.pop;

/**
 * メール受信Agent インタフェース.
 * <pre>
 * 専用のビルダ、PopAgentBuilder で、インスタンスを生成しメール受信を行う。
 *
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
 *
 * </pre>
 * @since 1.1.6
 */
public interface PopAgent{

	/**
	 * メール受信実行
	 * @return 受信した数
	 */
	public int recv();

}
