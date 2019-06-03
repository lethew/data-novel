package studio.greeks.data.novel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/19 21:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chapter implements Serializable {
    private ObjectId _id;
    private ObjectId nId;
    private Integer idx;
    private String name;
    private String url;
    private String content;
    private Integer wordsSize;
    private Date publishTime;
    private boolean free;
}
