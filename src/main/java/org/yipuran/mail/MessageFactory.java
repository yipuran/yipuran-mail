package org.yipuran.mail;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

/**
 * メールメッセージFactory.
 */
public class MessageFactory{
	/**
	 * メールメッセージ作成.
	 * @param map メールテンプレートVelocityに渡すマップ
	 * @param templateLoadpath テンプレートファイルパス
	 * @param templateName テンプレートファイル名
	 * @param templateInEncode テンプレート読込みエンコード
	 * @param templateOutEncode 出力エンコード
	 * @return メールメッセージ
	 */
	public String create(Map<String,Object> map, String templateLoadpath, String templateName, String templateInEncode, String templateOutEncode){
		String message = null;
		try(StringWriter sw = new StringWriter()){
			Properties prop = new Properties();
			prop.setProperty("resource.loader", "file,class");
			prop.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
			prop.setProperty("file.resource.loader.path", templateLoadpath);
			prop.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
			prop.setProperty("input.encoding", templateInEncode);
			prop.setProperty("output.encoding", templateOutEncode);
			// velocity ログを出力させない設定！
			prop.setProperty("runtime.log.logsystem.class","org.apache.velocity.runtime.log.NullLogSystem");
			// Velocity初期化
			VelocityEngine engine = new VelocityEngine();
			engine.init(prop);
			Template template = engine.getTemplate(templateName);
			VelocityContext context = new VelocityContext();  // VelocityContext
			// 置換えMapをVelocityContextにセット
			if (map != null){
				Set<String> keySet = map.keySet();
				for (Iterator<String> it=keySet.iterator();it.hasNext();){
					String key = it.next();
					context.put(key,map.get(key));
				}
			}
			// メールテンプレートとマージ
			template.merge(context,sw);
			message = Encode.convProphibitCode(Encode.convCp932toJIS(sw.toString()));
		}catch(ResourceNotFoundException e){
			throw e;
		}catch(ParseErrorException e){
			throw e;
		}catch(Exception e){
			throw new RuntimeException("◆ Exception :"+e.getMessage(),e);
      }
		return message;
	}
}
