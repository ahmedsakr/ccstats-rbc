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

package com.rbccca.input;


/**
 *
 * @author Ahmed Sakr
 * @since November 20, 2015
 */
public abstract class Argument<T> implements ArgumentBehavior<T> {

    private String name;
    private T value;
    private T[] abnormalArguments;

    /**
     * Argument constructor without abnormal arguments.
     * Initializes the argument and performs certain tests to determine the state of the argument.
     *
     * @param name      The Name the argument has been tagged with.
     * @param arguments The runtime arguments obtained from the main method.
     * @param index     The Index the current argument is held in the arguments array
     * @param class1    The class of the value.
     */
    public Argument(String name, String[] arguments, int index, Class<T> class1) {
        this.name = name;

        if (arguments.length <= index) {
            onArgumentOutOfBounds();
        }
        else if (arguments[index] == null || arguments[index].equalsIgnoreCase("null") || arguments[index].equals("")) {
            onIllegalArgument();
        } else {
            if (class1.equals(Boolean.class)) {
                if (arguments[index].equalsIgnoreCase("false") || arguments[index].equalsIgnoreCase("true")) {
                    this.value = class1.cast(Boolean.valueOf(arguments[index]));
                } else {
                    onIllegalArgument();
                }
            } else {
                this.value = class1.cast(arguments[index]);
            }
        }
    }

    /**
     * Argument constructor with abnormal arguments.
     * Calls the main constructor, with the addition of an extra test of whether the argument
     * is an abnormal argument.
     *
     * @param name      The Name the argument has been tagged with.
     * @param arguments The runtime arguments obtained from the main method.
     * @param index     The Index the current argument is held in the arguments array
     * @param class1    The class of the value.
     * @param abnormalArguments Abnormal arguments as varargs.
     */
    @SafeVarargs
    public Argument(String name, String[] arguments, int index, Class<T> class1, T... abnormalArguments) {
        this(name, arguments, index, class1);
        this.abnormalArguments = abnormalArguments;

        if (isAbnormalArgument()) {
            onAbnormalArgument();
        }
    }

    /**
     *
     * @return The string name of the argument.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return The value of the argument.
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * Overrides the value of the argument.
     *
     * @param value The new value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     *
     * @return The abnormal arguments.
     */
    @Override
    public T[] getAbnormalArguments() {
        return abnormalArguments;
    }


    /**
     * In the instance where the argument is not in the arguments array.
     * To be overridden on construction of the class.
     */
    public abstract void onArgumentOutOfBounds();


    /**
     * In the instance where the argument's value has been found in the abnormal arguments array.
     * To be overridden on construction of the class.
     */
    public abstract void onAbnormalArgument();


    /**
     * In the instance where the argument's value is an illegal value.
     * To be overridden on construction of the class.
     */
    public abstract void onIllegalArgument();

}