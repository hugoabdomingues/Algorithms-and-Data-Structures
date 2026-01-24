import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Scene {
    private int id;                     // id da cena
    private String description;         // descrição da cena

    //definição de um tipo enumerado em java
    public enum Type {NORMAL, WON, FAILED}

    private Type type;
    //inicializar Dynvec -->vetor dinâmico
    private ArrayList<Option> options = new ArrayList<Option>(); /*A lista ArrayList<Option> options = new ArrayList<Option>(); é como uma "caixa" vazia dentro de cada cena. Cada cena tem a sua caixa própria, ou seja, as suas opções próprias..*/


    public Scene(int id, String description, Type type) {
        //TODO: implement
        if (id < 0 || description == null || description.isEmpty() || type == null) {
            throw new IllegalArgumentException("The scene number cannot be negative, the description cannot be null or empty and the type cannot be null");
        }
        this.id = id;
        this.description = description;
        this.type = type;
    }

    //método para guardar as opções da história no Arraylist Option criado acima

    /**
     * chega-nos uma option do parsescene inserir no Arraylist options criado acima
     * @param o Option uma opcao vinda do parsescene
     * @return boolean retorna true se adicionar a opcao com sucesso caso contrario false
     */
    public boolean addOption(Option o) {
        if (o == null) {                            //caso a opção seja null, n adicionamos ao Arraylist
            return false;
        }
        for (int i = 0; i < options.size(); i++) {  //percorremos o Arraylist options e verificamos se a Option o já faz parte desse array. Se sim, então já temos essa opção no array e retornamos false.
            if (options.get(i).equals(o)) {         //se o = options.get(i), então já guardámos essa opção previamente e n guardamos no Arraylist novamente --> return false
                return false;
            }
        }
        options.add(o); // se a Option o é diferente das opções todas do Arraylist options, então adicionamos ao Arraylist.
        return true;
    }


    public int getId() {
        return this.id;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Iterable<Option> getOptions()        //getter
    {
        return options;                         //return da lista
    }

//i
// toString devolve uma string com a representação textual do conteúdo da cena
/*
[<id>]
<descrição> (pode ter múltiplas linhas)
<opção1>
<opção2>
...*/
    //Não imprime diretamente para o ecrâ
    public String toString() {
        // Ao chamar options.get(i), o Java usa automaticamente a lista options da cena atual, inicializada no inicio da classe
        String sceneText = "[" + id + "]\n" + description + "\n"; //adicionamos o id e a descrição
        if (type == Type.NORMAL) {
            for (int i = 0; i < options.size(); i++) {
                sceneText = sceneText + options.get(i).toString() + "\n";     //adicionamos as opções da cena à mesma
            }
            sceneText = sceneText.substring(0, sceneText.length() - 1);        //retiramos o último \n após o loop

        } else if (type == Type.WON) {
            sceneText = sceneText + "<WON>";
        } else {
            sceneText = sceneText + "<FAILED>";
        }
        return sceneText;
    }

    //NOTA: não precisamos de um método para libertar a cena (como no projeto de LP)
    //porque a JVM vai ficar responsável pela libertação de memória dos objetos do tipo Cena
    //que já não forem usados


    public static Scene parseScene(BufferedReader reader) throws IOException, NumberFormatException {
        //ler a primeira linha --> id
        String line = reader.readLine();

        //isolar id:
        line = line.replace("[", "");
        line = line.replace("]", "");

        //declarar argumentos da Scene
        int id = Integer.parseInt(line);
        Type type = Type.NORMAL;
        String description = "";                    //iniciar uma string vazia..

        reader.readLine();                          //fazemos reader.readline para ler <<< mas não atribuímos à String linha, pois não queremos guardar <<< na description
        line = reader.readLine();                  // Lemos então o início da descrição e guardamos na String linha. Quando entramos no while guardamos esta "linha" na description e começamos o loop

        //leitura da descrição
        while (!line.equals(">>><WON>") && !line.equals(">>><FAILED>") && !line.equals(">>>")) {
            // adiciona a linha à descrição:
            description = description + line + "\n";
            line = reader.readLine();
        }
        //retirar \n a mais
        if (description.endsWith("\n")) {
            description = description.substring(0, description.length() - 1); // remove o último \n
        }

        if (line.equals(">>><WON>")) {
            type = Type.WON;            //atualizar type para WON
            return new Scene(id, description, type);
        } else if (line.equals(">>><FAILED>")) {
            type = Type.FAILED;
            return new Scene(id, description, type);
        }
        //Type.NORMAL
        Scene scene = new Scene(id, description, type);

        //ler opções
        reader.readLine();                                          //fazemos reader.realine() para ignorar os ***
        line = reader.readLine();                                   //1º opção --> Fazemos outro readline mas atribuimos à String linha a primeira opção
        while (line.startsWith("+")) {                              //1º opção entra no loop
            Option optionParse = Option.parseOption(line);          //criamos um objeto Option "optionParse" que vai transformar uma String num objeto com id e description
            scene.addOption(optionParse);                           //enviamos a Option para a função addOption acima
            line = reader.readLine();                               //lemos a prox linha, se começar com + ent é uma opção e repete
        }
        return scene;
    }
}

