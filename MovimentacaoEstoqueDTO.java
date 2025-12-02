package com.classes.DTO;

import java.time.LocalDateTime;

    public class MovimentacaoEstoqueDTO {
        private int id;
        private ProdutoDTO produto;
        private DoadorDTO doador;
        private String tipoMovimento;
        private int quantidade;
        private LocalDateTime dataMovimentacao;
        private String observacao;

        public MovimentacaoEstoqueDTO() {
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public ProdutoDTO getProduto() {
            return produto;
        }

        public void setProduto(ProdutoDTO produto) {
            this.produto = produto;
        }

        public DoadorDTO getDoador() {
            return doador;
        }

        public void setDoador(DoadorDTO doador) {
            this.doador = doador;
        }

        public String getTipoMovimento() {
            return tipoMovimento;
        }

        public void setTipoMovimento(String tipoMovimento) {
            this.tipoMovimento = tipoMovimento;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public LocalDateTime getDataMovimentacao() {
            return dataMovimentacao;
        }

        public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
            this.dataMovimentacao = dataMovimentacao;
        }

        public String getObservacao() {
            return observacao;
        }

        public void setObservacao(String observacao) {
            this.observacao = observacao;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MovimentacaoEstoqueDTO{");
            sb.append("id=").append(id);
            sb.append(", produto=").append(produto);
            sb.append(", doador=").append(doador);
            sb.append(", tipoMovimento='").append(tipoMovimento).append('\'');
            sb.append(", quantidade=").append(quantidade);
            sb.append(", dataMovimentacao=").append(dataMovimentacao);
            sb.append(", observacao='").append(observacao).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }