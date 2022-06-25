package org.yipuran.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * メール送信タスク.
 * <PRE>
 * java.util.concurrent.Callable 実装クラスとして生成され Callable の戻り値としてメール送信が成功したかどうかを返す。
 * Callable<Integer> の Integer値は、0 = メール送信成功、1 = メール送信失敗である。
 * </PRE>
 */
public class SendmailTask implements Callable<Integer>{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private MailSource mailSource;
	private Properties properties;
	private boolean smtpauth;
	private Transport transport;
	private String server;
	private String user;
	private String password;
	private boolean isNotification;
	private Throwable error;
	/**
	 * コンストラクタ.
	 * @param server SMTPメールサーバ
	 * @param port SMTP port-no
	 * @param smtpauth SMTP認証. true=行う
	 * @param user SMTP認証ユーザ
	 * @param passwd SMTP認証パスワード
	 * @param timeout SMTPタイムアウト(秒)
	 * @param isNotificvation 開封通知送信を求めるか否か(true=求める）
	 * @param mailSource メールメッセージ
	 */
	@Inject
	public SendmailTask(@Named("smtp-host") String server, @Named("smtp-port") String port, @Named("smtp-auth") boolean smtpauth, @Named("user") String user, @Named("password") String passwd
			, @Named("timeout") int timeout, @Named("notification") boolean isNotification,  MailSource mailSource){
		this.smtpauth = smtpauth;
		this.mailSource = mailSource;
		this.server = server;
		this.user = user;
		this.password = passwd;
		this.isNotification = isNotification;
		properties = new Properties();
		properties.put("mail.smtp.host", server);
		properties.put("mail.host", server);      // 接続するホスト名
		properties.put("mail.smtp.timeout", timeout * 1000);
		properties.put("mail.smtp.auth","true");
		properties.put("mail.smtp.auth", smtpauth ? "true" : "false");
		properties.put("mail.smtp.port", port);
	}

	@Override
	public Integer call() throws Exception{
		int rtn = 0;
		try{
			// SMTPセッション接続
			Session session=Session.getDefaultInstance(properties, null);
			// MimeMessage生成
			MimeMessage mimeMessage = createMimeMessage(session);

			// SMTPメール送信
			if (this.smtpauth){
				// SMTP認証が必要な場合
				transport = session.getTransport("smtp");
				transport.connect(server, user, password);
				transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
				transport.close();
			}else{
				Transport.send(mimeMessage);
			}
		}catch(Exception e){
			error = e;
			rtn = 1;
			logger.error(e.getMessage(), e);
		}
		return rtn;
	}
	/**
	 * @param session Session
	 * @return MimeMessage
	 * @throws UnsupportedEncodingException サポートされないエンコード
	 * @throws MessagingException MimeMessage生成エラー
	 */
	private MimeMessage createMimeMessage(Session session) throws UnsupportedEncodingException, MessagingException{
		MimeMessage mimeMessage = new MimeMessage(session);
		// メール作成
		Map<String,String> map;
		// FROM:
		map = mailSource.getFrommap();
		Iterator<String> itr=map.keySet().iterator();
		String fromaddres = itr.next();
		String fromalias = map.get(fromaddres);
		if (fromalias != null && fromalias.length() > 0){
			mimeMessage.setFrom(new InternetAddress(fromaddres,fromalias, "Shift_JIS"));
			// 返信先あれば設定
			if (mailSource.getReplyto() != null){
				mimeMessage.setReplyTo(new Address[]{new InternetAddress(mailSource.getReplyto())});
			}
		}else{
			mimeMessage.setFrom(new InternetAddress(fromaddres));
			// 返信先あれば設定
			if (mailSource.getReplyto() != null){
				mimeMessage.setReplyTo(new Address[]{new InternetAddress(mailSource.getReplyto())});
			}
		}
		if (isNotification) mimeMessage.setHeader("Disposition-Notification-To", fromaddres);
		// TO:
		mimeMessage = setMimeAddres(mimeMessage, mailSource.getTomap(), Message.RecipientType.TO);
		// Subject:
		mimeMessage.setSubject(mailSource.getSubject(), "Shift_JIS");
		// メール内容（添付あり／なしで本文セットが異なる）
		List<File> templist = mailSource.getTemplist();
		if (templist.size() > 0){
			// 添付あり
			MimeBodyPart txtmbp = new MimeBodyPart();
			txtmbp.setText(mailSource.getMessage(), "Shift_JIS");
			txtmbp.setHeader("Content-Type","text/plain; charset=" + "Shift_JIS");
			MimeMultipart multipart = new MimeMultipart(); // マルチパートオブジェクト
			multipart.addBodyPart(txtmbp);
			for(Iterator<File> it=templist.iterator();it.hasNext();){
				MimeBodyPart mbp2 = new MimeBodyPart();
				// 添付するファイル名を指定
				FileDataSource fds = new FileDataSource(it.next());
				mbp2.setDataHandler(new DataHandler(fds));
				//mbp2.setFileName(MimeUtility.encodeWord(fds.getName()));
				mbp2.setFileName(MimeUtility.encodeWord(fds.getName(), "Windows-31J", "B"));
				// ボディパートを追加
				multipart.addBodyPart(mbp2);
			}
			// マルチパートオブジェクトをメッセージに設定
			mimeMessage.setContent(multipart);
		}else{
			// 添付なし
			mimeMessage.setText(mailSource.getMessage(), "Shift_JIS");
			mimeMessage.setHeader("Content-Type","text/plain; charset=" + "Shift_JIS");
			mimeMessage.setHeader("Content-Transfer-Encoding","quoted-printable");
		}
		mimeMessage.setSentDate(new Date());   // 送信日付
		return mimeMessage;
	}
	/**
	 * MimeMessage 作成.
	 * @param mime MimeMessage
	 * @param map MimeMessageのmap
	 * @param type RecipientType
	 * @return MimeMessage
	 * @throws MessagingException MimeMessageエラー
	 * @throws UnsupportedEncodingException エンコードエラー
	 */
	@SuppressWarnings("unlikely-arg-type")
	private MimeMessage setMimeAddres(MimeMessage mime,Map<String,String> map,Message.RecipientType type) throws MessagingException,UnsupportedEncodingException{
		int mapsize = map.size();
		if (mapsize==0){ return mime; }
		MimeMessage rtn = mime;
		String[] addres = new String[mapsize];
		String[] alias  = new String[mapsize];
		int i=0;
		for(Iterator<String> it=map.keySet().iterator();it.hasNext();i++){
			addres[i] = it.next();
			alias[i] = map.get(addres);
		}
		rtn.setRecipients(type,addres[0]);
		if (alias[0] != null && alias[0].length() > 0){
			rtn.setRecipient(type,new InternetAddress(addres[0],alias[0], "Shift_JIS"));
		}
		for(int k=1;k < addres.length;k++){
			rtn.addRecipients(type,addres[k]);
			if (alias[k] != null && alias[k].length() > 0){
				rtn.addRecipient(type,new InternetAddress(addres[k],alias[k], "Shift_JIS"));
			}
		}
		return rtn;
	}
	/**
	 * メール送信エラー情報の取得.
	 * @return Throwable
	 */
	public Throwable getError(){
		return error;
	}
}
