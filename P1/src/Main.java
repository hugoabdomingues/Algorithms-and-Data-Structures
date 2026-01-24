import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // carregar a história a partir do file

        Story story = Story.parseStory("exemplo.txt");

        if (story == null) {
            System.out.println("Erro ao carregar a história.");
            return;
        }

        story.interactivePlay();
    }
}
