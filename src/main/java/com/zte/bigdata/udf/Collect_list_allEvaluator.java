
package com.zte.bigdata.udf;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class Collect_list_allEvaluator extends GenericUDAFEvaluator
        implements Serializable {

    private static final long serialVersionUID = 1l;

    enum BufferType { SET, LIST }

    // For PARTIAL1 and COMPLETE: ObjectInspectors for original data
    private transient PrimitiveObjectInspector inputOI;
    // For PARTIAL2 and FINAL: ObjectInspectors for partial aggregations (list
    // of objs)
    private transient StandardListObjectInspector loi;

    private transient ListObjectInspector internalMergeOI;

    private BufferType bufferType;

    //needed by kyro
    public Collect_list_allEvaluator() {
    }

    public Collect_list_allEvaluator(BufferType bufferType){
        this.bufferType = bufferType;
    }

    @Override
    public ObjectInspector init(GenericUDAFEvaluator.Mode m, ObjectInspector[] parameters)
            throws HiveException {
        super.init(m, parameters);
        // init output object inspectors
        // The output of a partial aggregation is a list
        if (m == GenericUDAFEvaluator.Mode.PARTIAL1) {
            inputOI = (PrimitiveObjectInspector) parameters[0];
            return ObjectInspectorFactory
                    .getStandardListObjectInspector((PrimitiveObjectInspector) ObjectInspectorUtils
                            .getStandardObjectInspector(inputOI));
        } else {
            if (!(parameters[0] instanceof ListObjectInspector)) {
                //no map aggregation.
                inputOI = (PrimitiveObjectInspector)  ObjectInspectorUtils
                        .getStandardObjectInspector(parameters[0]);
                return (StandardListObjectInspector) ObjectInspectorFactory
                        .getStandardListObjectInspector(inputOI);
            } else {
                internalMergeOI = (ListObjectInspector) parameters[0];
                inputOI = (PrimitiveObjectInspector) internalMergeOI.getListElementObjectInspector();
                loi = (StandardListObjectInspector) ObjectInspectorUtils.getStandardObjectInspector(internalMergeOI);
                return loi;
            }
        }
    }


    class MkArrayAggregationBuffer extends AbstractAggregationBuffer {

        private Collection<Object> container;

        public MkArrayAggregationBuffer() {
            if (bufferType == Collect_list_allEvaluator.BufferType.LIST){
                container = new ArrayList<Object>();
            } else if(bufferType == Collect_list_allEvaluator.BufferType.SET){
                container = new LinkedHashSet<Object>();
            } else {
                throw new RuntimeException("Buffer type unknown");
            }
        }
    }

    @Override
    public void reset(GenericUDAFEvaluator.AggregationBuffer agg) throws HiveException {
        ((Collect_list_allEvaluator.MkArrayAggregationBuffer) agg).container.clear();
    }

    @Override
    public GenericUDAFEvaluator.AggregationBuffer getNewAggregationBuffer() throws HiveException {
        Collect_list_allEvaluator.MkArrayAggregationBuffer ret = new Collect_list_allEvaluator.MkArrayAggregationBuffer();
        return ret;
    }

    //mapside
    @Override
    public void iterate(GenericUDAFEvaluator.AggregationBuffer agg, Object[] parameters)
            throws HiveException {
        assert (parameters.length == 1);
        Object p = parameters[0];

//        if (p != null) {
            Collect_list_allEvaluator.MkArrayAggregationBuffer myagg = (Collect_list_allEvaluator.MkArrayAggregationBuffer) agg;
            putIntoCollection(p, myagg);
//        }
    }

    //mapside
    @Override
    public Object terminatePartial(GenericUDAFEvaluator.AggregationBuffer agg) throws HiveException {
        Collect_list_allEvaluator.MkArrayAggregationBuffer myagg = (Collect_list_allEvaluator.MkArrayAggregationBuffer) agg;
        List<Object> ret = new ArrayList<Object>(myagg.container.size());
        ret.addAll(myagg.container);
        return ret;
    }

    @Override
    public void merge(GenericUDAFEvaluator.AggregationBuffer agg, Object partial)
            throws HiveException {
        Collect_list_allEvaluator.MkArrayAggregationBuffer myagg = (Collect_list_allEvaluator.MkArrayAggregationBuffer) agg;
        List<Object> partialResult = (ArrayList<Object>) internalMergeOI.getList(partial);
        if (partialResult != null) {
            for(Object i : partialResult) {
                putIntoCollection(i, myagg);
            }
        }
    }

    @Override
    public Object terminate(GenericUDAFEvaluator.AggregationBuffer agg) throws HiveException {
        Collect_list_allEvaluator.MkArrayAggregationBuffer myagg = (Collect_list_allEvaluator.MkArrayAggregationBuffer) agg;
        List<Object> ret = new ArrayList<Object>(myagg.container.size());
        ret.addAll(myagg.container);
        return ret;
    }

    private void putIntoCollection(Object p, Collect_list_allEvaluator.MkArrayAggregationBuffer myagg) {
        Object pCopy = ObjectInspectorUtils.copyToStandardObject(p,  this.inputOI);
        myagg.container.add(pCopy);
    }

    public Collect_list_allEvaluator.BufferType getBufferType() {
        return bufferType;
    }

    public void setBufferType(Collect_list_allEvaluator.BufferType bufferType) {
        this.bufferType = bufferType;
    }

}