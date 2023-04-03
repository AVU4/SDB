package itmo.lab.sdb.postgres;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MOEXIndexResult {
    private Long dayId;
    private String title;
    private String summary;
    private List<Double> indexValues;
}
