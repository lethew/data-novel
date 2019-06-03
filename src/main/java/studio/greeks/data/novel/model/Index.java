package studio.greeks.data.novel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/19 21:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Index implements Serializable {
    private ObjectId _id;
    private String type;
    private String name;
    private String abstractUrl;
    private String author;
    private String status;
    private Long totalClick;
    private Long totalWord;
    private String summary;
    private String[] tags;
    private String authorDesc;
}
