package org.yipuran.mail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * SendmailTaskビルダ.
 * <PRE>コンストラクタのパラメータ、 propertiesFilePath メール設定 Properties ファイルのパスは、&#064;Inject と
 * &#064;INamed("MAIL_PROPERTIES_PATH")が着いているので
 * Google guice インジェクトで渡すことができる。その場合、
 *     binder().bind(String.class).annotatedWith(Names.named("MAIL_PROPERTIES_PATH")).toInstance(path);
 * で渡す。
 * </PRE>
 */
public final class SendMailTaskBuilder{
	String propertiesFilePath;

	/**
	 * コンストラクタ.
	 * @param propertiesFilePath メール設定 Properties ファイルのパス
	 */
	@Inject
	public SendMailTaskBuilder(@Named("MAIL_PROPERTIES_PATH") String propertiesFilePath){
		this.propertiesFilePath = propertiesFilePath;
	}
	/**
	 * メール送信タスク生成（開封通知指定なし）.
	 * @param fromaddres from
	 * @param replyTo replyTo
	 * @param toaddres to宛先
	 * @param subject 件名
	 * @param message メッセージ
	 * @param file 添付ファイル（省略、または複数指定可能）
	 * @return
	 */
	public SendmailTask create(String fromaddres, String replyTo, String toaddres, String subject, String message, File...file){
		return create( fromaddres,  replyTo,  toaddres,  subject,  message,  false, file);
	}
	/**
	 * メール送信タスク生成（開封通知は任意指定）.
	 * @param fromaddres from
	 * @param replyTo replyTo
	 * @param toaddres to宛先
	 * @param subject 件名
	 * @param message メッセージ
	 * @param notification  開封通知送信を求めるか否か(true=求める）
	 * @param file 添付ファイル（省略、または複数指定可能）
	 * @return
	 */
	public SendmailTask create(String fromaddres, String replyTo, String toaddres, String subject, String message, final boolean notification, File...file){
		final MailSource source = new MailSource();
		source.setFrom(fromaddres, null);
		source.setReplyto(replyTo);
		source.setTo(toaddres, null);
		source.setSubject(subject);
		source.setMessage(message);
		source.setTemp(file);
		Injector injector = Guice.createInjector(new AbstractModule(){
			@Override
			protected void configure(){
				Properties properties = new Properties();
				try(FileReader fr = new FileReader(propertiesFilePath)){
					properties.load(fr);
				}catch(IOException e){
					throw new RuntimeException(e);
				}
				Names.bindProperties(binder(), properties);
				binder().bind(MailSource.class).toInstance(source);
				binder().bind(boolean.class).annotatedWith(Names.named("notification")).toInstance(notification);
			}
		});
		return injector.getInstance(SendmailTask.class);
	}
}
