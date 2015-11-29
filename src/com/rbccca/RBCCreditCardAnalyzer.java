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
        CreditStatement statement = null;
        String outputPath = "RBCCCA_stats.txt";
        boolean outputStatistics, userProvidedOutputPath;

        Argument<String> filename = new Argument<String>("filename", args, 0, String.class, "help") {

            @Override
            public void onArgumentOutOfBounds() {
                System.out.println("The 1st argument (" + getName() + ") has not been provided. Exiting...");
                System.exit(0);
            }

            @Override
            public void onAbnormalArgument() {
                System.out.println("RBCCreditCardAnalyzer requires at least one argument in order to run:"
                        + "\n1. (Required) The path of the credit card history HTML file."
                        + "\n2. (Optional) Boolean value of outputting the statistics. Default value: \"false\""
                        + "\n3. (Optional) The output path for the statistics text file. Default value:" +
                        " \"RBCCCA_stats.txt\".");

                System.exit(0);
            }

            @Override
            public void onIllegalArgument() {
                System.out.println("The 1st argument (" + getName() + ") is an illegal filename argument. Exiting...");
                System.exit(0);
            }
        };

        Argument<Boolean> output = new Argument<Boolean>("should_output", args, 1, Boolean.class) {

            @Override
            public void onArgumentOutOfBounds() {
                System.out.println("The 2nd argument (" + getName() + ") has not been provided. Default value false.");
                setValue(false);
            }

            @Override
            public void onAbnormalArgument() {

            }

            @Override
            public void onIllegalArgument() {
                System.out.println("The 2nd argument (" + getName() + ") has an illegal output argument" +
                        " (value must be false or true). Exiting...");
                System.exit(0);
            }
        };

        Argument<String> outputFile = new Argument<String>("output_filename", args, 2, String.class) {

            @Override
            public void onArgumentOutOfBounds() {
                if (output.getValue() != null && output.getValue()) {
                    System.out.println("The 3rd argument (" + getName() + ") has not been supplied, and the 2nd" +
                            " argument has been specified to true. Exiting...");
                    System.exit(0);
                }
            }

            @Override
            public void onAbnormalArgument() {

            }

            @Override
            public void onIllegalArgument() {
                if (output.getValue() != null &&  output.getValue()) {
                    System.out.println("The 3rd argument (" + getName() + ") has an illegal filename value." +
                            " However, the 2nd argument has been specified to true." +
                            " Default value for 3rd argument has been set to \"RBCCCA_stats.txt\" ");
                    setValue("RBCCCA_stats.txt");
                } else {
                    System.out.println("The 3rd argument (" + getName() + ") has an illegal filename value" +
                            ". Exiting...");
                    System.exit(0);
                }
            }
        };

        try {
            statement = new CreditStatement(filename.getValue());
            outputStatistics = output.getValue();
            outputPath = outputFile.getValue();
        } catch (InvalidStatementPathException e) {
            e.printStackTrace();
        }
    }
}