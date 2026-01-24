import java.io.*;
import java.util.Scanner;
import java.util.Set;

public class Story {

    //definicao de constantes em java são feitas assim
    //Como convenção costuma usar-se letras maiúsculas como nome de variáveis que são constantes
    //para que seja fácil perceber no código quando se usa uma constante
    //a keyword final indica que depois de definido, o valor não pode ser mais alterado
    private static final String WAITING = "WAITING";
    private static final String PROMPT = "Select your option: ";
    private static final String RESET_CMD = "RESET";
    private static final String SAVE_CMD = "SAVE";
    private static final String LOAD_CMD = "LOAD";
    private static final String PRINT_CMD = "PRINT";

    private static final String FILE_ERROR = "Error: invalid file or error opening file!";
    private static final String NUMBER_FORMAT_ERROR = "Error: invalid number format when parsing!";
    private static final String STDIN_ERROR = "Error: cannot access or read from standard input";

    private static final String INVALID_OPTION = "Invalid option. Such option does not exist.";
    private static final String INVALID_NUMBER = "Invalid option. Please enter a number of an option, or one of the commands (PRINT,RESET,LOAD,SAVE).";
    private static final String INVALID_LOAD_STORY_NAME = "The loaded story name does not match. Loading aborted.";
    private static final String INVALID_LOAD_SCENE = "The scene in the loaded file is not valid. Loading aborted.";

    private static final String LOAD_SUCCESS = "Story state loaded successfully.";
    private static final String SAVE_SUCCESS = "Story state saved successfully.";

    private String title;
    private Node <Scene>[] nodes;
    private Node <Scene> current;
    private BufferedReader reader;


    public Story(String title, Node<Scene>[] nodes)
    {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title null or empty");
        }
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException("Nodes null or nodes length equals 0");
        }
        this.title = title;
        this.nodes = nodes;
        this.current = nodes[0];
        this.reader = new BufferedReader(new InputStreamReader(System.in));


    }

    public String getTitle()
    {
        return title;
    }

    public Node<Scene> getCurrent()
    {
        return current;
    }

    /**
     * reset para a cena 0: recomeça a história
     */
    public void reset()
    {
        this.current = nodes[0];
    }

    /**
     * função cujo objetivo é dar SAVE no progresso do jogo, ou seja, no current content
     * @param fileName ficheiro lido através da main e passado esta função
     * @return true se o SAVE foi bem sucedido e false se o save n tenha sido bem sucedido
     */
    public boolean saveStoryState(String fileName)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            //escrever o título na primeira linha
            writer.write(this.title + "\n");
            //escrever o Id da cena na segunda linha
            writer.write(Integer.toString(this.current.getContent().getId()));
            System.out.println(SAVE_SUCCESS);
            return true;
        }catch (IOException close) {
            System.out.println(FILE_ERROR);
            return false;
        }

    }
    /**
     *Chamamos esta função a partir do interactiveplay para dar LOAD num SAVE do jogo
     * @param fileName ficheiro que vem da main (neste caso é o exemplo.txt
     * @return vai treturnar true caso o LOAD tenho sido bem sucedido e false caso um erro tenha acontecido
     */
    public boolean loadStoryState(String fileName)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String titleFile = reader.readLine();   //título

            try {       //tentamos converter para int, caso n seja possível --> catch
                String idString = reader.readLine();    //ler id da cena SAVED

                int idInt = Integer.parseInt(idString); //cast do id para int

                if (!this.title.equals(titleFile)) {    //comparação do título do ficheiro com o título da nossa história
                    System.out.println(INVALID_LOAD_STORY_NAME);
                    return false;

                } else {
                    //procurams nos nodes a cena com o id correspondente ao id que guardámos e atualizamos o current node
                    for (int i = 0; i < nodes.length; i++) {                                /*se o idInt é igual ao id das cenas... atualiza o current para essa cena. Se não for igual a nenhum dos id's da cena ent n consta na lista de id's --->*/
                        //comparamos o id do ficheiro com o id da cena
                        if (idInt == nodes[i].getContent().getId()) {
                            //atualizar current para o node que tem o id da cena que veio no ficheiro
                            current = nodes[i];
                            System.out.println(LOAD_SUCCESS);
                            return true;
                        }
                    }
                }
                System.out.println(INVALID_LOAD_SCENE);
                return false;

            }catch (NumberFormatException parsingError) {
                System.out.println(NUMBER_FORMAT_ERROR);
                return false;
            }
        }catch (IOException readingError){
            System.out.println(FILE_ERROR);
            return false;

        }
    }

    public void nonInteractivePlay() {
        try {
            while (current.getContent().getType() == Scene.Type.NORMAL) {
                String input = reader.readLine();                       //usamos o reader do construtor para o SI

                if (input == null || input.isEmpty()) {
                    System.out.println(WAITING);
                    return;
                }
                int choice;
                //opção selecionada a partir do SI
                choice = Integer.parseInt(input);
                //inicializamos um node que contém uma scene
                Node<Scene> nextNode = null;
                for (Node<Scene> neighbour : current.getNeighbours()) {     //1 2
                    if (neighbour.getContent().getId() == choice) {         //compara choice com cada Id dos vizinhos de current
                        nextNode = neighbour;
                        break;           // break para evitar continuar a iterar mesmo depois da condição ser true
                    }
                }
                if (nextNode != null) {
                    current = nextNode;
                } else {
                    System.out.println(WAITING);
                    return;
                }
                if (current.getContent().getType() == Scene.Type.WON) {
                    System.out.println("WON");
                } else if (current.getContent().getType() == Scene.Type.FAILED) {
                    System.out.println("FAILED");
                }
            }
        } catch(IOException input) {
            System.out.println(STDIN_ERROR);
        }
    }
    public void interactivePlay() {
        try {
            System.out.println(title);
            System.out.println(current.getContent());       //imprimir CENA

            while (current.getContent().getType() == Scene.Type.NORMAL) {
                System.out.printf(PROMPT);
                try {
                    String standardInput = reader.readLine();       //ler o Std Input
                    //reset
                    if (standardInput.equals(RESET_CMD)) {
                        reset();
                        System.out.println(current.getContent());
                        //print
                    } else if (standardInput.equals(PRINT_CMD)) {
                        System.out.println(current.getContent());
                        //save
                    } else if (standardInput.startsWith(SAVE_CMD)) {
                        String token[] = standardInput.split(" ");
                        if (token.length != 2 || !token[0].equals("SAVE")) {
                            System.out.println(FILE_ERROR);
                        } else {
                            saveStoryState(token[1]);
                        }
                        //Load
                    } else if (standardInput.startsWith(LOAD_CMD)) {
                        String[] parts = standardInput.split(" ");
                        if (parts.length != 2 || !parts[0].equals("LOAD")) {
                            System.out.println(FILE_ERROR);
                        } else {
                            if (loadStoryState(parts[1])) {                 // se não fizermos if true, o programa vai retornar um true que vai ser ignorado
                                System.out.println(current.getContent());
                            }
                        }
                    } else {
                        try {                                                           //como n sabemos se o input do user é um número ou não, fazemos um try catch. Se soubessemos que a string seria sempre um número, n tinhamos de fazer um try catch
                            int input = Integer.parseInt(standardInput);                // se for um int vai fazer cast, se não for --> catch
                            Node<Scene> nextNode = null;                                //variá´vel para armazenar o prox vizinho
                            for (Node<Scene> neighbour : current.getNeighbours()) {     //node neighbour corre a lista dos vizinhos
                                if (neighbour.getContent().getId() == input) {          //Para cada vizinho de current, verifica se o Id da cena dele (neighbour.getContent().getId()) é igual ao input.
                                    nextNode = neighbour;                               // se o id da cena do vizinho for igual ao input, vamos para a cena desse mesmo id e atualizamos o current node.
                                    break;
                                }
                            }
                            if (nextNode != null) {     //se o input n coincide com nenhum id das cenas dos vizinhos ent nextNode = null e lançamos INVALID_OPTION
                                current = nextNode;
                                System.out.println(current.getContent());
                            } else {
                                System.out.println(INVALID_OPTION);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(INVALID_NUMBER);
                        }
                    }
                } catch (IOException e) {
                    System.out.println(STDIN_ERROR);
                    return;
                }
            }
        } catch (Exception er) {
            System.out.println(STDIN_ERROR);
        }
    }

    public static Story parseStory(String filename)             //parsestory = converter para um objeto do tipo Story com title, nodes, e current
    {
        String title;
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            title = br.readLine();                    //ler a primeira linha --> "Floresta do deserto" --> título
            line = br.readLine();                     //ler número de cenas --> 4

            int sceneNumber = Integer.parseInt(line);               //converter 4 para int
            @SuppressWarnings("unchecked")
            Node<Scene>[] node = new Node[sceneNumber];

            for (int i = 0; i < sceneNumber; i++) {
                Scene scene = Scene.parseScene(br);                 //enviamos o buffered reader para scene e retornamos UMA scene
                node[i] = new Node<>(scene);                       // metemos essa scene num node --> os nodes têm agora "content" armazenado, faltam os vizinhos
            }
            //vizinhos
            for (int i = 0; i < sceneNumber; i++) {
                for (Option options : node[i].getContent().getOptions()){               //percorremos as opções do node[i] e guardamos uma de cada vez no int abaixo
                    int optionsId = options.getId();                                    //vamos buscar o id da opção
                    for (int j = 0; j < sceneNumber; j++) {
                        if (node[j].getContent().getId() == optionsId) {                //aqui comparamos o id da opção com o id da cena do node[j], se o id da opção for igual ao da cena ent é um vizinho desse node
                            node[i].addNeighbour(node[j]);                              //se o id da cena do node[j] = id da opção do node[i], adicionamos o node[j] como vizinho do node[i]
                            break;           // Sai após encontrar o vizinho
                        }
                    }
                }
            }
            return new Story(title, node);

        } catch (IOException file_error) {
            System.out.println(FILE_ERROR);
            return null;
        } catch (NumberFormatException number_error) {
            System.out.println(NUMBER_FORMAT_ERROR);
            return null;
        }
    }
}