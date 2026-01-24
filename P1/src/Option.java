public class Option {
    private int id;
    private String description;

    //TODO: implement
    public Option(int id, String description) //Construtor
    {
        if (id < 0 || description == null || description.isEmpty()) {
            throw new IllegalArgumentException("The Id cannot be negative and the description cannot be null or empty.");
        }
        this.id = id;
        this.description = description;
    }


    //TODO: implement
    public int getId()
    {
        return id;
    }

    //TODO: implement
    public String getDescription()
    {
        return description;
    }

    //TODO: implement
    @Override
    public boolean equals(Object o) { // Usado em Scene.addOption para evitar adicionar opções com id igual

        if (o instanceof Option) {
            Option currentOption = (Option) o;          //parsing é o mesmo que casting
                                                                                                    // duas Option são iguais se tiverem o mesmo id e a mesma description
            return id == currentOption.id && description.equals(currentOption.description);         //se id == currentOption.id && description.equals(currentOption.description --> return true
        }
        return false;
    }
    // Converter o objeto para string
    public String toString()
    {
        return "+ " + id + ". " + description;                              //não é preciso dizer this.id e this.description?
    }
    //
    //Parse significa analisar ou extrair
    public static Option parseOption(String s) throws NumberFormatException
    {
        String limpa = s.substring(2);  //Começamos a partir do index 2 para podermos retirar o "+ "
        //divisão da String
        String[] token = limpa.split("\\. ", 2);         /*dividir a string EM 2 PARTES usando o ponto final --> colocamos "\\. " pois
                                                                    precisa de duas barras \\ para conseguir ler o ponto em regex */
        String leftId = token[0];                                           // id isolado --> 1
        String description = token[1];                                      // descrição

        int id = Integer.parseInt(leftId);

        return new Option(id, description);

        /* example of the format of an option when parsing
        + 0. Voltar para trás.
         */
    }
}