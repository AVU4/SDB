package itmo.lab.sdb.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "index")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IndexData {
    @Id
    private String tradeDate;
    private String open;
    private String high;
    private String low;
    private String close;
    private String value;
}
