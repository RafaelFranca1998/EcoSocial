package com.example.rafael_cruz.prototipo.config;
@Deprecated
public class ItemListView {

    private String textoDescricao,textoLocalidade;
    private int iconeRid;

    public ItemListView() {
        this("","", -1);
    }

    public ItemListView(String textoDescricao,String textoLocalidade, int iconeRid) {
        this.textoDescricao     = textoDescricao;
        this.textoLocalidade    = textoLocalidade;
        this.iconeRid           = iconeRid;
    }

    public int getIconeRid() {
        return iconeRid;
    }

    public void setIconeRid(int iconeRid) {
        this.iconeRid = iconeRid;
    }

    public String getTextoLocalidade() {
        return textoLocalidade;
    }

    public void setTextoLocalidade(String textoLocalidade) {
        this.textoLocalidade = textoLocalidade;
    }

    public String getTextoDescricao() {
        return textoDescricao;
    }

    public void setTextoDescricao(String textoDescricao) {
        this.textoDescricao = textoDescricao;
    }
}