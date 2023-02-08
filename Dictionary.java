package ex2;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    private Map<String, String> book;
    public Dictionary() {
        book = new HashMap<>();
    }

    public Dictionary (Map<String, String> map) {
        book = new HashMap<>(map);
    }

    public void setBook(Map<String, String> book) {
        this.book = book;
    }

    
    
    public void AddTerm(String term, String meanning) throws Error {
        if (term.equals("")||meanning.equals(""))
            throw new Error("Values can't be empty");
        if (term.matches("[a-zA-Z]+")) 
            book.put(term, meanning);
        else 
            throw new Error("Invalid term");
    }
    
    

    public boolean isExist(String term) {
        return this.book.containsKey(term);
    }

    public void DeleteTerm(String term) {
        book.remove(term);
    }

    public Map<String, String> getBook() {
        return book;
    }

    public Map<String, String> Search(String searchCriteria) {
        Map<String, String> data = new HashMap<>();
        for (Map.Entry<String, String> entry : this.book.entrySet()) {
            String term = entry.getKey().split(" ")[0];
            String meanning = entry.getValue();
            if (term.startsWith(searchCriteria)) {
                data.put(entry.getKey(), entry.getValue());
            }
        }
        return data;
    }
}
