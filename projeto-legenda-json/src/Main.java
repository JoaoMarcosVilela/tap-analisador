import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;

public class Main {

	public static void main(String[] args) {
		
		File diretorio = new File("resultados");
		if(!diretorio.exists()) {
			diretorio.mkdir();
		}
		for(int i = 1; i<10 ; i++) {
			try {
				String diretorioAtual = System.getProperty("user.dir");
				String localArquivo = diretorioAtual.substring(0, diretorioAtual.indexOf("projeto-legenda-json"));
				
				String regexEps = "vikings-first-season/Vikings.S01E0"+ i +".1080p.WEB-DL.AC3.X264-MRSK.srt";
				Path arquivo = Paths.get(localArquivo+regexEps);
				
				List<String> linhas = Files.readAllLines(arquivo);
				Map<String, Integer> contagemPalavras = new HashMap<>();
				
				linhas.stream().filter(
                        linha -> !(linha.matches("\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}")))
                        .map(linha -> linha.replaceAll("[<][i][>]|[<][/][i][>]", ""))
                        .map(linha -> linha.replaceAll("<font color|</font>", ""))
                        .map(linha -> linha.replaceAll("[^a-zA-Z'’\s]", "").toLowerCase())
                        .flatMap(linha -> Pattern.compile("\\b[a-zA-Z'’]+\\b").matcher(linha).results())
                        .map(MatchResult::group)
                        .forEach(linha -> contagemPalavras.merge(linha, 1, Integer::sum));
				

				
				List<Map.Entry<String, Integer>> listaPalavras = new ArrayList<>(contagemPalavras.entrySet());
				listaPalavras.sort((par_chave_valor1, par_chave_valor2) -> par_chave_valor2.getValue().compareTo(par_chave_valor1.getValue()));

	                List<Frequencia> palavrasFrequencia = listaPalavras.stream()
	                		.map(par_chave_valor -> new Frequencia(par_chave_valor.getKey(), par_chave_valor.getValue()))
	                		.collect(Collectors.toList());
	            
	                Gson gson = new Gson();
	                String json = gson.toJson(palavrasFrequencia);
	                
	                try(BufferedWriter writer = new BufferedWriter(
	                		new FileWriter("resultados/Vikings.S01E0"+ i + ".1080p.WEB-DL.AC3.X264-MRSK.srt.json"))){
	                	writer.write(json);
	                	System.out.println("EP0"+i+" Gerado");
	                }catch(Exception e) {
	                	e.printStackTrace();
	                }
                
			}catch (Exception e) {
				System.out.println("Erro");
			}
		}
	}
}
