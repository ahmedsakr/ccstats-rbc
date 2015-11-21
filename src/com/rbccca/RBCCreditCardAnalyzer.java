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
        String filePath = null, outputPath = "RBCCCA_stats.txt";
        boolean outputStatistics;

        switch (args.length) {
            case 0:
                System.out.println("No runtime arguments provided. If you need help regarding the usage of RBCCCA, "
                        + "execute the application with \"help\" as the argument. Exiting...");
                System.exit(0);
                break;
            case 1:
                if (!args[0].equalsIgnoreCase("help")) {
                    filePath = args[0];

                    if (filePath == null || filePath.isEmpty() || filePath.equalsIgnoreCase("null")) {
                        System.out.println("No Input File has been specified in the runtime arguments. Exiting...");
                        System.exit(0);
                    }
                } else {
                    System.out.println("RBCCreditCardAnalyzer requires at least one argument in order to run:"
                            + "\n1. (Required) The path of the credit card history HTML file."
                            + "\n2. (Optional) Boolean value of outputting the statistics. Default value: \"false\""
                            + "\n3. (Optional) The output path for the statistics text file. Default value:" +
                            " \"RBCCCA_stats.txt\".");

                    System.exit(0);
                }
                break;
            case 2:
                filePath = args[0];
                outputStatistics = Boolean.valueOf(args[1]);

                if (filePath == null || filePath.isEmpty() || filePath.equalsIgnoreCase("null")) {
                    System.out.println("No Input File has been specified in the runtime arguments. Exiting...");
                    System.exit(0);
                }

                break;
            case 3:
                filePath = args[0];
                outputStatistics = Boolean.valueOf(args[1]);
                outputPath = args[2];

                if (filePath == null || filePath.isEmpty() || filePath.equalsIgnoreCase("null")) {
                    System.out.println("No Input File has been specified in the runtime arguments. Exiting...");
                    System.exit(0);
                }

                if (args[2] == null || args[2].isEmpty() || args[2].equalsIgnoreCase("null")) {
                    System.out.println("The 3rd runtime argument (output file path) has not been provided. " +
                            "By default, it has been assigned as \"RBCCCA_stats.txt\"");
                }

                break;
            default:
                System.out.println("abnormal amount of program arguments. Exiting...");
                System.exit(0);
        }
    }
}