package itmo.lab.sdb.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BusinessNews {
    private String title;
    private String score;
    private String link;
    private String summary;
    private String published;
    private List<String> tickers;
}
