
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

interface IBingo {
    void sortearNumero();
    void criarNovaCartela(String nome);
    void atualizarHistorico();
}

class Cartela {
    private String dono;
    private java.util.List<Integer> numeros;
    private java.util.List<Integer> marcados;

    public Cartela(String dono, java.util.List<Integer> numeros) {
        this.dono = dono;
        this.numeros = numeros;
        this.marcados = new ArrayList<>();
    }

    public String getDono() {
        return dono;
    }

    public java.util.List<Integer> getNumeros() {
        return numeros;
    }

    public void marcarNumero(int n) {
        if (numeros.contains(n) && !marcados.contains(n)) {
            marcados.add(n);
        }
    }

    public boolean venceu() {
        return marcados.size() == numeros.size();
    }

    public String toString() {
        return "Cartela de " + dono + ": " + numeros.toString();
    }
}

class Bingo {
    private java.util.List<Integer> numerosSorteados;
    private java.util.List<Cartela> cartelas;
    private Random random;

    public Bingo() {
        numerosSorteados = new ArrayList<>();
        cartelas = new ArrayList<>();
        random = new Random();
    }

    public int sortearNumero() {
        if (numerosSorteados.size() >= 75) {
            return -1;
        }
        int numero;
        do {
            numero = random.nextInt(75) + 1;
        } while (numerosSorteados.contains(numero));
        numerosSorteados.add(numero);

        // Marca o número em todas as cartelas
        for (Cartela c : cartelas) {
            c.marcarNumero(numero);
        }
        return numero;
    }

    public java.util.List<Integer> getNumerosSorteados() {
        return numerosSorteados;
    }

    public void adicionarCartela(Cartela c) {
        cartelas.add(c);
    }

    public java.util.List<Cartela> getCartelas() {
        return cartelas;
    }

    public Cartela verificarVencedor() {
        for (Cartela c : cartelas) {
            if (c.venceu()) {
                return c;
            }
        }
        return null;
    }
}

public class MainBingo extends JFrame implements IBingo {
    private Bingo bingo;
    private JTextArea areaHistorico;
    private JPanel painelCartelas;
    private JLabel numeroSorteado;
    private JLabel vencedorLabel;

    public MainBingo() {
        bingo = new Bingo();

        setTitle("🎱 Bingo Interativo - ToyoService Edition");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 30));

        setLayout(new BorderLayout(10, 10));

        // Topo
        JLabel titulo = new JLabel("🎯 Bingo Digital", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        add(titulo, BorderLayout.NORTH);

        // Painel principal
        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2, 10, 10));
        painelPrincipal.setBackground(new Color(30, 30, 30));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Painel da esquerda - histórico e botões
        JPanel painelEsquerdo = new JPanel(new BorderLayout(10, 10));
        painelEsquerdo.setBackground(new Color(40, 40, 40));

        numeroSorteado = new JLabel("Nenhum número sorteado ainda", SwingConstants.CENTER);
        numeroSorteado.setFont(new Font("Segoe UI", Font.BOLD, 22));
        numeroSorteado.setForeground(new Color(255, 220, 100));
        painelEsquerdo.add(numeroSorteado, BorderLayout.NORTH);

        areaHistorico = new JTextArea();
        areaHistorico.setEditable(false);
        areaHistorico.setBackground(new Color(25, 25, 25));
        areaHistorico.setForeground(Color.WHITE);
        areaHistorico.setFont(new Font("Consolas", Font.PLAIN, 14));
        painelEsquerdo.add(new JScrollPane(areaHistorico), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setBackground(new Color(40, 40, 40));
        JButton botaoNovaCartela = new JButton("➕ Nova Cartela");
        JButton botaoSortear = new JButton("🎲 Sortear Número");

        estilizarBotao(botaoNovaCartela);
        estilizarBotao(botaoSortear);

        painelBotoes.add(botaoNovaCartela);
        painelBotoes.add(botaoSortear);

        painelEsquerdo.add(painelBotoes, BorderLayout.SOUTH);

        // Painel da direita - cartelas
        painelCartelas = new JPanel();
        painelCartelas.setLayout(new BoxLayout(painelCartelas, BoxLayout.Y_AXIS));
        painelCartelas.setBackground(new Color(30, 30, 30));
        JScrollPane scrollCartelas = new JScrollPane(painelCartelas);
        painelPrincipal.add(painelEsquerdo);
        painelPrincipal.add(scrollCartelas);

        add(painelPrincipal, BorderLayout.CENTER);

        // Rodapé
        vencedorLabel = new JLabel("", SwingConstants.CENTER);
        vencedorLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        vencedorLabel.setForeground(Color.GREEN);
        add(vencedorLabel, BorderLayout.SOUTH);

        // Eventos
        botaoNovaCartela.addActionListener(e -> criarNovaCartela(null));
        botaoSortear.addActionListener(e -> sortearNumero());

        setVisible(true);
    }

    private void estilizarBotao(JButton botao) {
        botao.setBackground(new Color(70, 70, 200));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void criarNovaCartela(String nome) {
        if (nome == null) {
            nome = JOptionPane.showInputDialog(this, "Nome do dono da cartela:");
            if (nome == null || nome.trim().isEmpty()) return;
        }

        java.util.List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        while (numeros.size() < 15) {
            int n = r.nextInt(75) + 1;
            if (!numeros.contains(n)) numeros.add(n);
        }
        Collections.sort(numeros);
        Cartela cartela = new Cartela(nome, numeros);
        bingo.adicionarCartela(cartela);

        JLabel lbl = new JLabel(cartela.toString());
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Consolas", Font.PLAIN, 14));
        lbl.setBorder(new EmptyBorder(5, 5, 5, 5));
        painelCartelas.add(lbl);
        painelCartelas.revalidate();
        painelCartelas.repaint();
    }

    @Override
    public void sortearNumero() {
        int n = bingo.sortearNumero();
        if (n == -1) {
            JOptionPane.showMessageDialog(this, "Todos os 75 números já foram sorteados!");
            return;
        }

        numeroSorteado.setText("Número sorteado: " + n);
        atualizarHistorico();

        Cartela vencedor = bingo.verificarVencedor();
        if (vencedor != null) {
            vencedorLabel.setText("🏆 Vencedor: " + vencedor.getDono() + "!");
            JOptionPane.showMessageDialog(this, "🎉 O vencedor é " + vencedor.getDono() + "!");
        }
    }

    @Override
    public void atualizarHistorico() {
        StringBuilder sb = new StringBuilder();
        java.util.List<Integer> nums = bingo.getNumerosSorteados();
        for (int i = 0; i < nums.size(); i++) {
            sb.append(String.format("%02d ", nums.get(i)));
            if ((i + 1) % 10 == 0) sb.append("\n");
        }
        areaHistorico.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainBingo::new);
    }
}
