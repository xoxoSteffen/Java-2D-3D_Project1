//Author: Steffen Herweg
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class VertLoader {

    // list of components, with list of points
    public static List<List<Vec2>> load(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));

        
        List<String> cleaned = new ArrayList<>();
        for (String s : lines) {
            s = s.trim();
            if (!s.isEmpty()) cleaned.add(s);
        }

        int idx = 0;

        int numComponents = Integer.parseInt(cleaned.get(idx++));
        List<List<Vec2>> components = new ArrayList<>();

        for (int c = 0; c < numComponents; c++) {
            int n = Integer.parseInt(cleaned.get(idx++));
            List<Vec2> pts = new ArrayList<>(n);

            for (int i = 0; i < n; i++) {
                String[] parts = cleaned.get(idx++).split("\\s+");
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                pts.add(new Vec2(x, y));
            }
            components.add(pts);
        }

        return components;
    }
}
