package com.example.ussd1.util;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberUtil {

    public static String createMusoniRequest(String mobileNo, String mobileNoSecondary){

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

        return s;
    }

}
