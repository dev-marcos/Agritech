package bean;

public class Condicao {   
    private int id;
    private int idProgramacao;
    private int tipoDado;
    private int logica;
    private String valor1;
    private int condicao;
    private String valor2;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdProgramacao() {
        return idProgramacao;
    }
    public void setIdProgramacao(int idProgramacao) {
        this.idProgramacao = idProgramacao;
    }
    
    public int getTipoDado() {
        return tipoDado;
    }
    public void setTipoDado(int tipoDado) {
        this.tipoDado = tipoDado;
    }
    public int getLogica() {
        return logica;
    }
    public void setLogica(int logica) {
        this.logica = logica;
    }
    public String getValor1() {
        return valor1;
    }
    public void setValor1(String valor1) {
        this.valor1 = valor1;
    }
    public int getCondicao() {
        return condicao;
    }
    public void setCondicao(int condicao) {
        this.condicao = condicao;
    }
    public String getValor2() {
        return valor2;
    }
    public void setValor2(String valor2) {
        this.valor2 = valor2;
    }
}

