package org.yipuran.mail.pop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

/**
 * メール受信Agent実装
 */
class PopAgentImpl implements PopAgent{
	@Inject @Named("HOST")     private String host;
	@Inject @Named("USER")     private String user;
	@Inject @Named("PASSWORD") private String passwd;
	@Inject private PopHandler handle;           // 受信情報格納ハンドラ
	private int attachnum = 1;
	private List<String> listTempName;           // 添付ファイルPath リスト
	private RecvMailBean rmbean;

	@Override
	public int recv(){
		Logger logger = LoggerFactory.getLogger(this.getClass());
		Session session = Session.getDefaultInstance(System.getProperties(),null);
		try(Store store = session.getStore("pop3");Folder folder = store.getFolder("INBOX")){
			// 接続
			store.connect(this.host,-1,this.user,this.passwd);
			folder.open(Folder.READ_WRITE);
			// フォルダーにあるメッセージの数を取得→０なら即終了
			int totalMessages = folder.getMessageCount();
			if (totalMessages == 0) {
				logger.debug("■ メールは 0件です");
				folder.close(false);
				this.handle.term();
				return 0;
			}
			logger.debug("■ メールは "+totalMessages+"件です");

			// メッセージを取得 LOOP
			Message[] messages = folder.getMessages();
			for(int i=0;i < messages.length;i++){
				this.listTempName = new ArrayList<String>();   // 添付ファイルPath リスト初期化
				this.rmbean = new RecvMailBean();
				// メッセージ parse
				this.messageParse(messages[i]);
				// 添付ありなら、RecvMailBean に通知
				if (this.listTempName.size() > 0){
					File[] ft = new File[this.listTempName.size()];
					int ct=0;
					for(Iterator<String> it=this.listTempName.iterator();it.hasNext();ct++){
						ft[ct] = new File(it.next());
					}
					this.rmbean.setFiles(ft);
				}
				// 削除フラグセット
				messages[i].setFlag(Flags.Flag.DELETED,true);
				// メール格納ハンドラ実行
				this.handle.store(this.rmbean);
			}
			// フォルダー CLOSE
			folder.close(true);
			store.close();
			// メール受信の後処理実行＝受信情報格納ハンドラのterm()を実行
			this.handle.term();

			return totalMessages;
		}catch(Exception e){
			logger.error("◆ Exception : "+e.getMessage(),e);
		}
		return -1;    // エラー発生
	}
	private void messageParse(Part p) throws Exception{
		Logger logger = LoggerFactory.getLogger(this.getClass());
		Address[] a;
		boolean attachment = false;       // 添付有無判定　初期化
		String html = "";
		// ヘッダ情報の取得
		if (p instanceof Message){
			Message msg = (Message)p;
			// 差出人認識
			if ((a=msg.getFrom())!=null){
				String[] froms = new String[a.length];
				for(int i=0;i < a.length;i++){
					froms[i] = MimeUtility.decodeText(a[i].toString());
				}
				this.rmbean.setFroms(froms);
			}
			// 宛先：TO
			if ((a=msg.getRecipients(Message.RecipientType.TO))!=null){
				String[] recipients = new String[a.length];
				for(int i=0;i < a.length;i++){
					recipients[i] =  MimeUtility.decodeText(a[i].toString());
				}
				this.rmbean.setRecipientsTo(recipients);
			}
			if ((a=msg.getRecipients(Message.RecipientType.CC))!=null){
				String[] recipients = new String[a.length];
				for(int i=0;i < a.length;i++){
					recipients[i] =  MimeUtility.decodeText(a[i].toString());
				}
				this.rmbean.setRecipientsCc(recipients);
			}
			// 題名
			this.rmbean.setSubject(msg.getSubject());
			// 日付
			this.rmbean.setSendDate(msg.getSentDate());
			// サイズ
			this.rmbean.setSize(msg.getSize());
		}
		// 添付有無の判定 ＆ Bodyの取得
		if (p.isMimeType("text/plain")){
			// 添付なし  テキスト
			this.rmbean.setBody((String)p.getContent());
		}else if(p.isMimeType("multipart/*")){
			// 添付あり  or マルチパートの場合
			Multipart mp = (Multipart)p.getContent();
			int count = mp.getCount();
			for(int k=0;k < count;k++){
				messageParse(mp.getBodyPart(k));
			}
		}else if(p.isMimeType("message/rfc822")){
			// メッセージの場合
			messageParse((Part)p.getContent());
		}else if (p.isMimeType("text/html")){
			// HTMLの場合
			html = ".html";
			attachment = true;
		}else{
			// その他の場合
			attachment = true;
		}
		// 添付保存
		if (attachment){
			String disp = p.getDisposition();
			if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
				// 添付ファイル名 → filename
				String filename = p.getFileName();
				if (filename!=null){
					filename =  MimeUtility.decodeText(filename);
				}else{
					filename = "_tempHTML_" + this.attachnum++ + html;
				}
					// 添付ファイル 保存先← ハンドラ#assignTempDir()
					String tempDir = this.handle.assignTempDir(this.rmbean);
					File f = new File(tempDir+"/"+filename);
					if (f.exists()){
						logger.debug("■ 同名のファイル["+filename+"] は上書きします");
					}
				try(OutputStream os = new BufferedOutputStream(new FileOutputStream(f));InputStream is = p.getInputStream()){
					int c;
					while((c = is.read()) != -1){
						os.write(c);
					}
					this.listTempName.add(tempDir+"/"+filename);
					logger.debug("■ 添付ファイル  "+tempDir+"/"+filename+"を保存しました。");
				}catch(IOException e){
					logger.warn("◆ 添付ファイルの保存に失敗しました。"+e);
				}
			}
		}
	}
}
