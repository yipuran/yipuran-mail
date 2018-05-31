package org.yipuran.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MailSource.
 */
public class MailSource{
	private Map<String,String> frommap;         // 送信者Map
   private Map<String,String> tomap;           // TO:宛先Map
   private String replyto;      // reply-to: 返信先
   private String subject;      // Subject:
   private String message;      // メール送信内容
   private List<File> templist;       // 添付ファイルリスト FileのList
   private boolean queClose;            // メールキュー使用時の終了かを判定する。

   /**
    * constructor.
    */
   protected MailSource(){
      this.frommap = new HashMap<String,String>();
      this.tomap = new HashMap<String,String>();
      this.templist = new ArrayList<File>();
   }
   /**
    * @param addres String
    * @param alias String
    */
   protected void setFrom(String addres,String alias) {
      this.mailexpress(addres);
      this.frommap.put(addres,alias);
   }
   /**
    * @param addres String
    * @param alias String
    */
   protected void setTo(String addres,String alias) {
      this.mailexpress(addres);
      this.tomap.put(addres,alias);
   }
   /**
    * @return reply to
    */
   protected String getReplyto(){
      return this.replyto;
   }
   /**
    * @param replyto String
    */
   protected void setReplyto(String replyto){
      this.replyto = replyto;
   }
   /**
    * @param subject String
    */
   protected void setSubject(String subject){
      if (subject==null || subject.length()==0){
         throw new IllegalArgumentException("mail body Nothing!");
      }
      // TODO
      this.subject = Encode.convProphibitCode(Encode.convCp932toJIS(subject));
   }
   /**
    * @param message String
    */
   protected void setMessage(String message){
      if (message==null || message.length()==0){
         throw new IllegalArgumentException("mail body Nothing!");
      }
      this.message = message;
   }
   /**
    * @param files File[]
    */
   protected void setTemp(File...files){
      for(int i=0;i < files.length;i++){
         if (!files[i].exists()){
            throw new IllegalArgumentException("tempFile not exists!! : "+files[i].getPath());
         }
         this.templist.add(files[i]);
      }
   }

   /**
    * @return Map<String,String>
    */
   protected Map<String,String> getFrommap(){
      return this.frommap;
   }
   /**
    * @return String
    */
   protected String getMessage(){
      return this.message;
   }
   /**
    * @return String
    */
   protected String getSubject(){
      return this.subject;
   }
   /**
    * @return List<File>
    */
   protected List<File> getTemplist(){
      return this.templist;
   }
   /**
    * @return Map<String,String>
    */
   protected Map<String,String> getTomap(){
      return this.tomap;
   }
   /**
    * clearTo.
    */
   protected void clearTo(){
      this.tomap.clear();
   }
   /**
    * clearAll.
    */
   protected void clearAll(){
      this.tomap.clear();
      this.frommap.clear();
      this.message = null;
      this.subject = null;
      this.replyto = null;
      this.templist.clear();
   }
   /**
    * メール正規表現チェック.
    * @param str String
    */
   private void mailexpress(String str){
      Pattern objPtn=Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",Pattern.CASE_INSENSITIVE);
      Matcher matcher = objPtn.matcher(str);
      if (!matcher.matches()){
         throw new IllegalArgumentException("Illegal mail addres : "+str);
      }
   }
   public boolean isQueClose(){
      return this.queClose;
   }
   public void setQueClose(boolean queClose){
      this.queClose = queClose;
   }
}
