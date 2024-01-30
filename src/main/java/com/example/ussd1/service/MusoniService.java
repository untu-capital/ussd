package com.example.ussd1.service;

//import com.example.ussd1.exception.RestTemplateResponseErrorHandler;
import com.example.ussd1.entity.Industry;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class MusoniService {


    @Autowired
    private final RestTemplate restTemplate;

    @Value("${musoni.url}")
    private String musoniBaseURl;
    @Value("${musoni.username}")
    private String username;
    @Value("${musoni.password}")
    private String password;
    @Value("${musoni.X_FINERACT_PLATFORM_TENANTID}")
    private String X_FINERACT_PLATFORM_TENANTID;
    @Value("${musoni.X_API_KEY}")
    private String X_API_KEY;

    public MusoniService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // TODO: 31/1/2023 Set Headers
    public HttpHeaders setMusoniHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(username, password);
        headers.set("X-Fineract-Platform-TenantId", X_FINERACT_PLATFORM_TENANTID);
        headers.set("x-api-key", X_API_KEY);
        headers.set("Content-Type", "application/json");

        return headers;
    }

    public HttpEntity<String> setHttpEntity() {
        return new HttpEntity<>(setMusoniHeaders());
    }
    // TODO: 1/2/2023 Create Client

    public String getAllClients() {
        return restTemplate.exchange(musoniBaseURl + "clients", HttpMethod.GET, setHttpEntity(), String.class).getBody();
    }

    public String getClientById(String clientId) {
        return restTemplate.exchange(musoniBaseURl + "clients/" + clientId, HttpMethod.GET, setHttpEntity(), String.class).getBody();
    }

    // TODO: 31/1/2023 Get Client by phone number
    public String getClientByMobileNo(String mobileNo, String mobileNoSecondary) {
        final String s = """
                {
                    "filterRulesExpression": {
                        "condition": "OR",
                        "rules": [
                            {
                                "id": "mobileNo",
                                "field": "mobileNo",
                                "type": "string",
                                "input": "text",
                                "operator": "equal",
                                "value": %s
                            },
                            {
                                "id": "mobileNoSecondary",
                                "field": "mobileNoSecondary",
                                "type": "string",
                                "input": "text",
                                "operator": "equal",
                                "value": %s
                            }
                        ],
                        "valid": true
                    },
                    "responseParameters": [
                        {
                            "ordinal": 0,
                            "name": "id"
                        },
                        {
                            "ordinal": 1,
                            "name": "accountNo"
                        },
                        {
                            "ordinal": 2,
                            "name": "status"
                        },
                        {
                            "ordinal": 3,
                            "name": "mobileNo"
                        }
                    ],
                    "sortByParameters": [
                        {
                            "ordinal": 0,
                            "name": "id",
                            "direction": "ASC"
                        }
                    ]
                }
                """.formatted(mobileNo, mobileNoSecondary);
        String client, clientId;
        try {
            HttpEntity<String> entity = new HttpEntity<String>(s, setMusoniHeaders());
            client = restTemplate.exchange(musoniBaseURl + "datafilters/clients/queries/run-filter", HttpMethod.POST, entity, String.class).getBody();
            clientId = new JSONObject(new JSONObject(client).getJSONArray("pageItems").get(0).toString()).getBigInteger("id").toString();
            client = getClientById(clientId);

        } catch (JSONException e) {
            return "{ \"errorMessage\": \"Client with mobile number %s not found\"}".formatted(mobileNo);
        }
        return client;
    }

    public String getClientByQueryString(String queryString) {
        return restTemplate.exchange(musoniBaseURl + "clients?search=" + queryString, HttpMethod.GET, setHttpEntity(), String.class).getBody();
    }

    public String getAllLoans() {
        return restTemplate.exchange(musoniBaseURl + "loans", HttpMethod.GET, setHttpEntity(), String.class).getBody();
    }

    public String getLoanByLoanAccount(String loanAccount) {
        return restTemplate.exchange(musoniBaseURl + "loans/" + loanAccount, HttpMethod.GET, setHttpEntity(), String.class).getBody();
    }

    public String getLoanByQueryString(String queryString) {
        return restTemplate.exchange(musoniBaseURl + "loans?search=" + queryString, HttpMethod.GET, setHttpEntity(), String.class).getBody();
    }

    public String getMiniStatement(String loanAccount) {

        JSONObject loanSummary = new JSONObject(getLoanByLoanAccount(loanAccount)).getJSONObject("summary");
        System.out.println(loanSummary);
        return loanSummary.toString();
    }

    public String getRepaymentSchedule(String loanAccount) {
        String repaymentSchedule = restTemplate.exchange(musoniBaseURl + "loans/" + loanAccount + "?associations=repaymentSchedule", HttpMethod.GET, setHttpEntity(), String.class).getBody();
        System.out.println(repaymentSchedule);
        return repaymentSchedule;
    }

    public String getAllAssociations(String loanAccount) {
        String repaymentSchedule = restTemplate.exchange(musoniBaseURl + "loans/" + loanAccount + "?associations=repaymentSchedule", HttpMethod.GET, setHttpEntity(), String.class).getBody();
        System.out.println(repaymentSchedule);
        return repaymentSchedule;
    }

    List<String> clientAccounts = new ArrayList<>();
    public List<String> getClientLoans(String loanAccount) {
        String clientAccount = restTemplate.exchange(musoniBaseURl + "clients/" + loanAccount + "/accounts", HttpMethod.GET, setHttpEntity(), String.class).getBody();

        for (int i = 0; i<new JSONObject(clientAccount).getJSONArray("loanAccounts").length(); i++) {
            String clientAccountsList = new JSONObject(new JSONObject(clientAccount).getJSONArray("loanAccounts").get(i).toString()).getBigInteger("accountNo").toString();
            clientAccounts.add(clientAccountsList);
        }

        return clientAccounts;
    }


    public String getClientAccountsList(){
        StringBuilder menu = new StringBuilder("");
        int num = 1;
        for (String x : clientAccounts) {
            menu.append("\n").append(num).append(". ").append(x);
            num++;
        }
        return menu.toString();
    }


    public String currencyFormatter(BigDecimal amount) {
        Locale usa = new Locale("en", "US");
        NumberFormat currency = NumberFormat.getCurrencyInstance(usa);

        return currency.format(amount);
    }
}
