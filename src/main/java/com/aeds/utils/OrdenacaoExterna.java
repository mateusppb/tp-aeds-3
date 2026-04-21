package com.aeds.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.aeds.model.Consulta;


public class OrdenacaoExterna {

    public static void ordenarExternoPorData(String arquivoEntrada, String arquivoSaida, int tamanhoBloco) throws IOException {

        List<String> arquivosTemporarios = new ArrayList<>();

        RandomAccessFile raf = new RandomAccessFile(arquivoEntrada, "r");

        while (raf.getFilePointer() < raf.length()) {

            List<Consulta> bloco = new ArrayList<>();

            for (int i = 0; i < tamanhoBloco && raf.getFilePointer() < raf.length(); i++) {

                byte lapide = raf.readByte();
                int tamanho = raf.readInt();

                long pos = raf.getFilePointer();

                if (lapide == 0) {
                    Consulta c = new Consulta();
                    c.lerArquivo(raf, pos);
                    bloco.add(c);
                }

                raf.seek(pos + tamanho);
            }

            bloco.sort(Comparator.comparing(Consulta::getData));

            String nomeTemp = "temp_" + arquivosTemporarios.size() + ".db";
            arquivosTemporarios.add(nomeTemp);

            RandomAccessFile temp = new RandomAccessFile(nomeTemp, "rw");

            for (Consulta c : bloco) {
                c.escreverArquivo(temp);
            }

            temp.close();
        }

        raf.close();

        PriorityQueue<RegistroWrapper> heap = new PriorityQueue<>(
                Comparator.comparing(r -> r.consulta.getData())
        );

        List<RandomAccessFile> arquivos = new ArrayList<>();

        for (String nome : arquivosTemporarios) {
            RandomAccessFile temp = new RandomAccessFile(nome, "r");
            arquivos.add(temp);

            if (temp.length() > 0) {
                RegistroWrapper rw = lerProximo(temp);
                if (rw != null) heap.add(rw);
            }
        }

        RandomAccessFile saida = new RandomAccessFile(arquivoSaida, "rw");

        while (!heap.isEmpty()) {
            RegistroWrapper menor = heap.poll();

            menor.consulta.escreverArquivo(saida);

            RegistroWrapper proximo = lerProximo(menor.raf);
            if (proximo != null) {
                heap.add(proximo);
            }
        }

        for (RandomAccessFile f : arquivos) f.close();
        saida.close();

        for (String nome : arquivosTemporarios) {
            new File(nome).delete();
        }
    }

    static class RegistroWrapper {
        Consulta consulta;
        RandomAccessFile raf;

        public RegistroWrapper(Consulta c, RandomAccessFile raf) {
            this.consulta = c;
            this.raf = raf;
        }
    }

    private static RegistroWrapper lerProximo(RandomAccessFile raf) throws IOException {

        while (raf.getFilePointer() < raf.length()) {

            byte lapide = raf.readByte();
            int tamanho = raf.readInt();

            long pos = raf.getFilePointer();

            if (lapide == 0) {
                Consulta c = new Consulta();
                c.lerArquivo(raf, pos);

                raf.seek(pos + tamanho);
                return new RegistroWrapper(c, raf);
            }

            raf.seek(pos + tamanho);
        }

        return null;
    }
}