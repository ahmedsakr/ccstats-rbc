/**
 * Copyright (c) 2015 Ahmed Sakr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.rbccca;


import com.rbccca.analysis.Statistics;
import com.rbccca.analysis.data.Statement;
import com.rbccca.analysis.RBCHTMLDataExtractor;
import com.rbccca.exceptions.InvalidStatementPathException;
import com.rbccca.input.Argument;
import com.rbccca.input.CreditStatement;


/**
 *
 * @author Ahmed Sakr
 * @since November 18, 2015.
 */
public class RBCCreditCardAnalyzer {


    /**
     * Main entry point of the program.
     *
     * @param args Runtime arguments provided by the user.
     */
    public static void main(String[] args) {
        CreditStatement statementFile = null;
        String outputPath = "RBCCCA_stats.txt";

        Argument<String> filename = new Argument<String>("filename", args, 0, String.class, "help") {

            @Override
            public void onArgumentOutOfBounds() {
                System.out.printf("The 1st argument (%s) has not been provided. Exiting...\n", getName());
                System.exit(0);
            }

            @Override
            public void onAbnormalArgument() {
                System.out.println("RBCCreditCardAnalyzer requires at least one argument in order to run:"
                        + "\n1. (Required) The path of the credit card history HTML file."
                        + "\n2. (Optional) The output path for the statistics text file. Default value:" +
                        " \"RBCCCA_stats.txt\".");

                System.exit(0);
            }

            @Override
            public void onIllegalArgument() {
                System.out.printf("The 1st argument (%s) is an illegal filename argument. Exiting...\n", getName());
                System.exit(0);
            }
        };

        Argument<String> outputFile = new Argument<String>("output_filename", args, 2, String.class) {

            @Override
            public void onArgumentOutOfBounds() {
                System.out.printf("The 2nd argument (%s) has not been supplied, default value 'RBCCCA_stats.txt' \n"
                        , getName());
            }

            @Override
            public void onAbnormalArgument() {

            }

            @Override
            public void onIllegalArgument() {
                System.out.printf("The 2rd argument (%s) has an illegal filename value." +
                        " Defaulting to 'RBCCCA_stats.txt'\n", getName());
            }
        };

        try {
            statementFile = new CreditStatement(filename.getValue());
            outputPath = outputFile.getValue();
            Statement statement = new Statement(new RBCHTMLDataExtractor(statementFile));
        } catch (InvalidStatementPathException e) {
            e.printStackTrace();
        }
    }
}