package org.yipuran.mail.pop;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * 受信メールBean.
 * @since 1.1.6
 */
public class RecvMailBean implements Serializable{
	private static final long serialVersionUID = 1L;
	/** 差出人 */
	private String[] froms;
	/** 宛先 To */
	private String[] recipientsTo;
	/** 宛先 Cc */
	private String[] recipientsCc;
	/** メール件名 */
	private String subject;
	/** メールヘッダの時刻 */
	private Date sendDate;
	/** サイズ */
	private int size;
	/** メール本文 */
	private String body;
	/** 添付ファイル */
	private File[] files;

	public RecvMailBean(){
		this.froms = new String[0];
		this.recipientsTo = new String[0];
		this.recipientsCc = new String[0];
		this.subject = "無題";
		this.body = "";
		this.files = new File[0];
	}

	public String[] getFroms(){
		return this.froms;
	}
	public void setFroms(String[] froms){
		this.froms = froms;
	}
	public String[] getRecipientsTo(){
		return this.recipientsTo;
	}
	public void setRecipientsTo(String[] recipientsTo){
		this.recipientsTo = recipientsTo;
	}
	public String[] getRecipientsCc(){
		return this.recipientsCc;
	}
	public void setRecipientsCc(String[] recipientsCc){
		this.recipientsCc = recipientsCc;
	}
	public String getSubject(){
		return this.subject;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}
	public Date getSendDate(){
		return this.sendDate;
	}
	public void setSendDate(Date sendDate){
		this.sendDate = sendDate;
	}
	public int getSize(){
		return this.size;
	}
	public void setSize(int size){
		this.size = size;
	}
	public String getBody(){
		return this.body;
	}
	public void setBody(String body){
		this.body = body;
	}
	public File[] getFiles(){
		return this.files;
	}
	public void setFiles(File[] files){
		this.files = files;
	}
}
