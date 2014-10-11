/*
 * Copyright 2013-2014 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kylinolap.job.hadoop.invertedindex;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.ShortWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.kylinolap.common.util.ByteArray;
import com.kylinolap.job.constant.BatchConstants;

/**
 * @author yangli9
 */
public class IIDistinctColumnsReducer extends Reducer<ShortWritable, Text, NullWritable, Text> {

    private String[] columns;

    @Override
    protected void setup(Context context) throws IOException {
        Configuration conf = context.getConfiguration();
        this.columns = conf.get(BatchConstants.TABLE_COLUMNS).split(",");
    }

    @Override
    public void reduce(ShortWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String columnName = columns[key.get()];

        HashSet<ByteArray> set = new HashSet<ByteArray>();
        for (Text textValue : values) {
            ByteArray value = new ByteArray(Bytes.copy(textValue.getBytes(), 0, textValue.getLength()));
            set.add(value);
        }

        Configuration conf = context.getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        String outputPath = conf.get(BatchConstants.OUTPUT_PATH);
        FSDataOutputStream out = fs.create(new Path(outputPath, columnName));

        try {
            for (ByteArray value : set) {
                out.write(value.data);
                out.write('\n');
            }
        } finally {
            out.close();
        }

    }

}
