package team.travel.travelplanner.aspect;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ucar.ma2.Array;
import ucar.ma2.Range;
import ucar.ma2.RangeIterator;

/**
 * Speed up reading GRIB files by reducing the allocations due to Boxing in DataReceiver.addData()
 * Some of the code in the class was adapted from netcdf-java and can be found at
 * https://github.com/Unidata/netcdf-java/blob/e4ce8525fae979de0ecbf21a82268a5ccdbcb81d/grib/src/main/java/ucar/nc2/grib/collection/GribDataReader.java#L394
 * LICENSE of netcdf-java
 * BSD 3-Clause License
 * Copyright (c) 1998-2023, University Corporation for Atmospheric Research/Unidata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
@Aspect
public class DataReceiverAspect {
    @Around(value = "execution(* ucar.nc2.grib.collection.GribDataReader.DataReceiverIF+.addData(..)) && args(data, resultIndex, nx)", argNames = "pjp,data,resultIndex,nx")
    public Object addData(ProceedingJoinPoint pjp, float[] data, int resultIndex, int nx) throws Throwable {
        RangeIterator yRange = (RangeIterator) FieldUtils.readField(pjp.getThis(), "yRange", true);
        RangeIterator xRange = (RangeIterator) FieldUtils.readField(pjp.getThis(), "xRange", true);
        if (yRange instanceof Range yR && xRange instanceof Range xR) {
            Array dataArray = (Array) FieldUtils.readField(pjp.getThis(), "dataArray", true);
            int horizSize = (int) FieldUtils.readField(pjp.getThis(), "horizSize", true);

            int start = resultIndex * horizSize;
            int count = 0;
            for (int y = yR.first(); y <= yR.last(); y += yR.stride()) {
                for (int x = xR.first(); x <= xR.last(); x += xR.stride()) {
                    int dataIdx = y * nx + x;
                    dataArray.setFloat(start + count, data[dataIdx]);
                    count++;
                }
            }
            return null;
        } else {
            return pjp.proceed();
        }
    }
}
