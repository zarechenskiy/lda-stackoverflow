package ru.spbu.lda;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LDA {

    private final File stopWords;
    private final int numThreads;
    private final int numIterations;

    public LDA(@NotNull File stopWords, int numThreads, int numIterations) {
        this.stopWords = stopWords;
        this.numThreads = numThreads;
        this.numIterations = numIterations;
    }

    @NotNull
    public ParallelTopicModel createTopicModel(@NotNull InstanceList instances, int countTopics) throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(countTopics, 1.00, 0.01);
        model.addInstances(instances);

        model.setNumThreads(numThreads);
        model.setNumIterations(numIterations);
        model.estimate();

        return model;
    }

    @NotNull
    public InstanceList createInstanceList(@NotNull List<String> texts) {
        List<Pipe> pipes = createPipes();

        InstanceList instanceList = new InstanceList(new SerialPipes(pipes));
        instanceList.addThruPipe(new ArrayIterator(texts));

        return instanceList;
    }

    @NotNull
    private List<Pipe> createPipes() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequenceRemoveStopwords(stopWords, "UTF-8", false, false, false));
        pipes.add(new TokenSequence2FeatureSequence());

        return pipes;
    }
}
