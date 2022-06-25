package org.yipuran.mail.pop;

/**
 * メール受信データハンドラ インターフェース.
 * <pre>
 * PopAgnetが実行するメール受信ハンドラのインターフェース
 *
 * PopAgentBuilderで、PopAgent 生成時に本インターフェースを実装するクラスを指定する。
 * </pre>
 */
public interface PopHandler{

   /**
    * PopAgnetが実行する受信データ取得.
    * <pre>
    * メール１件受信につき１回実行される。
    * </pre>
    * @param rmbean 受信メールデータBean.
    * @since 1.1.6
    */
   public void store(RecvMailBean rmbean);

   /**
    * メール受信終了処理.
    * <pre>
    * PopAgentが全てのメール受信、本インターフェースの store 実行後、
    * 全ての受信メールが処理された後に実行する処理
    * メール０件の時、storeが実行されなくても term() は実行される。
    * </pre>
    * @since 1.1.6
    */
   public void term();

   /**
    * 添付ファイル保存先Path の取得.
    * <pre>
    * メールが実行されて、添付がある場合のみ、
    * 本インターフェースの store が実行される前に実行される。
    * 添付有りメール１個の受信につき、必ず１回呼び出される。
    * メール毎の添付ファイル保存先 PATH を返却する必要がある。
    * 【注意】
    * 　引数 RecvMailBeanは、filesフィールドが空のRecvMailBean＝受信メール情報
    * 　従って、このメソッドの実行で、RecvMailBean の 添付情報ファイル情報フィールド
    * 　に依存した処理はできず、他のRecvMailBean属性に依存した処理しかできない。
    * 　store で渡されるRecvMailBean 引数は、添付がある場合に、
    * 　添付のFile 情報が格納されている。
    * </pre>
    * @param rmbean filesフィールドが空のRecvMailBean＝受信メール情報
    * @return 受信メール固有の添付ファイル保存先Path
    * @since 1.1.6
    */
   public String assignTempDir(RecvMailBean rmbean);

}
