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

package com.rbccca.input.arguments;


/**
 *
 * @author Ahmed Sakr
 * @since November 20, 2015.
 */
public interface ArgumentBehavior<T> {

    T getValue();
    T[] getAbnormalArguments();

    /**
     * Loops over the abnormal arguments array, and checks whether the value specified in the argument
     * is contained in the array.
     *
     * @return  True    If the argument value is abnormal.
     *          False   Otherwise.
     */
    default boolean isAbnormalArgument() {
        if (getAbnormalArguments() == null) {
            return false;
        }

        for (T o : getAbnormalArguments()) {
            if (o.equals(getValue())) {
                return true;
            }
        }

        return false;
    }

    void onArgumentOutOfBounds();

    void onAbnormalArgument();

    void onIllegalArgument();
}