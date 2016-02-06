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

package com.ccstats.input;


import com.ccstats.exceptions.InvalidStatementPathException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 * @author Ahmed Sakr
 * @since November 28, 2015.
 */
public class CreditStatement {

    private Path filePath;


    /**
     * Constructor for the CreditStatement class.
     *
     * Initializes the path of the credit statement acquired from the arguments.
     * A few tests are performed in order to insure the file to be analyzed later on is an acceptable
     * file.
     *
     * @param filePath The path of the credit statement.
     * @throws InvalidStatementPathException
     */
    public CreditStatement(String filePath) throws InvalidStatementPathException {
        this.filePath = Paths.get(filePath);

        if (!Files.exists(this.filePath)) {
            throw new InvalidStatementPathException("The path of the file specified does not exist!");
        }

        if (!this.filePath.toString().endsWith(".html") && !this.filePath.toString().endsWith(".htm")) {
            throw new InvalidStatementPathException("File specified has an invalid file extension"
                    + " (.html or .htm only!)");
        }
    }


    /**
     * Return the local path of the credit statement file.
     *
     * @return The local path.
     */
    public String getLocalPath() {
        if (filePath.isAbsolute()) {
            return filePath.getFileName().toString();
        } else {
            return filePath.toString();
        }
    }


    /**
     * Return the absolute (standalone) path of the credit statement file.
     *
     * @return The Absolute path.
     */
    public String getAbsolutePath() {
        if (filePath.isAbsolute()) {
            return filePath.toString();
        } else {
            return filePath.toAbsolutePath().toString();
        }
    }
}