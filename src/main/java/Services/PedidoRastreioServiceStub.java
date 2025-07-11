package Services;

import Interface.InterfacePedidoRastreioService;

public class PedidoRastreioServiceStub implements InterfacePedidoRastreioService {

    public String getDadosRastreio(String codigoRastreio) {
        return "{" +
            "\"codigo\": 100," +
            "\"status\": \"Em trânsito\","+
            "\"transportadora\": \"Log Express\"" +
            "\"dataPostagem\": \"2025-06-25T15:00:00\"," +
            "\"previsaoEntrega\": \"2025-07-07\"," +
            "\"cronologia\": [" +
                "{" +
                    "\"data\": \"2025-06-28T16:40:00\"," +
                    "\"local\": \"Curitiba - PR\"," +
                    "\"status\": \"Chegou na unidade\"" +
                "},"+
                "{" +
                    "\"data\": \"2025-06-29T18:10:00\"," +
                    "\"local\": \"Curitiba - PR\"," +
                    "\"status\": \"Saiu da unidade\"" +
                "}" +
            "]" +
        "}";
    }
}
