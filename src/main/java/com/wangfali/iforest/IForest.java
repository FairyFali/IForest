package com.wangfali.iforest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IForest {

    public List<ITree> subTrees;
    public double[][] samples;
    public int trees;
    public int subSampleSize;

    public double center0, center1;

    public IForest(){
        subTrees = new ArrayList<>();
    }

    public static IForest createIForest(double[][] samples, int trees, int subSampleSize) throws Exception {
        // 方法参数合法性检验
        if (samples == null || samples.length == 0) {
            throw new Exception("Samples is null or empty, please check...");
        } else if (trees <= 0) {
            throw new Exception("Number of subtree t must be a positive...");
        } else if (subSampleSize <= 0) {
            throw new Exception("subSampleSize must be a positive...");
        }

        int rows = samples.length;
        int cols = samples[0].length;
        // 限制高度
        int limitHeight = (int)Math.ceil(Math.log(subSampleSize) / Math.log(2));

        Random random = new Random(System.currentTimeMillis());
        // 随机获取子树
        IForest iForest = new IForest();
        iForest.samples = samples;
        iForest.trees = trees;
        iForest.subSampleSize = subSampleSize;
        for(int i=0;i<trees;i++){
            double[][] subSamples = new double[subSampleSize][cols];
            for (int j=0;j<subSampleSize;j++) {
                int r = random.nextInt(rows);
                System.out.print(r + " ");
                subSamples[j] = samples[r];
            }
            System.out.println();
            ITree iTree = ITree.createITree(subSamples, 0, limitHeight);
            iForest.subTrees.add(iTree);
        }

        return iForest;
    }

    public double[] score(IForest iForest) throws Exception{
        int rows = iForest.samples.length;
        int cols = iForest.samples[0].length;
        int trees = iForest.trees;
        double[][] samples = iForest.samples;
        // 计算所有样本的异常值
        double[] scores = new double[rows];
        for(int i=0;i<rows;i++){
            double pathLenSum = 0;
            for(int j=0;j<trees;j++) {
                pathLenSum += ITree.pathLength(samples[i], iForest.subTrees.get(j));
            }
            double pathLenAvg = pathLenSum/trees;
            System.out.print(pathLenAvg+" ");
            double cn = ITree.c(iForest.subSampleSize);
            scores[i] = Math.pow(2, -(pathLenAvg/cn));
        }
        System.out.println();
        return scores;
    }

    public int[] sampleLabel(double[] scores, int iters){
        // 聚类样本的异常值
        int[] labels = classifyByCluster(scores, iters);
        return labels;
    }

    /**
     * 预测样本
     * @param sample
     * @return
     * @throws Exception
     */
    public int predict(double[] sample) throws Exception{
        double pathLenSum = 0;
        for(int j=0;j<trees;j++) {
            pathLenSum += ITree.pathLength(sample, subTrees.get(j));
        }
        double pathLenAvg = pathLenSum/trees;
        //System.out.print(pathLenAvg+" ");
        double cn = ITree.c(subSampleSize);
        double score = Math.pow(2, -(pathLenAvg/cn));

        return Math.abs(score - center0) > Math.abs(score - center1)?0:1;
    }

    /**
     * 聚类
     * @param scores
     * @param iters
     * @return
     */
    private int[] classifyByCluster(double[] scores, int iters) {

        // 两个聚类中心
        this.center0 = scores[0]; // 异常类的聚类中心
        this.center1 = scores[0]; // 正常类的聚类中心

        // 根据原论文，异常指数接近1说明是异常点，接近0为正常点。所以，将center0、center1分别初始化为
        // scores中的最大值和最小值。这样就相当于KMeans聚类的初始点的选择，解决了KMeans聚类的不稳定性。
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > center0) {
                center0 = scores[i];
            }

            if (scores[i] < center1) {
                center1 = scores[i];
            }
        }

        int cnt0, cnt1;
        double diff0, diff1;
        int[] labels = new int[scores.length];

        // 迭代聚类(迭代iters次)
        for (int n = 0; n < iters; n++) {
            // 判断每个样本的类别
            cnt0 = 0;
            cnt1 = 0;

            for (int i = 0; i < scores.length; i++) {
                // 计算当前点与两个聚类中心的距离
                diff0 = Math.abs(scores[i] - center0);
                diff1 = Math.abs(scores[i] - center1);

                // 根据与聚类中心的距离，判断类标
                if (diff0 < diff1) {
                    labels[i] = 0;
                    cnt0++;
                } else {
                    labels[i] = 1;
                    cnt1++;
                }
            }

            // 保存旧的聚类中心
            diff0 = center0;
            diff1 = center1;

            // 重新计算聚类中心
            center0 = 0.0;
            center1 = 0.0;
            for (int i = 0; i < scores.length; i++) {
                if (labels[i] == 0) {
                    center0 += scores[i];
                } else {
                    center1 += scores[i];
                }
            }

            center0 /= cnt0;
            center1 /= cnt1;

            // 提前迭代终止条件
            if (center0 - diff0 <= 1e-6 && center1 - diff1 <= 1e-6) {
                break;
            }
        }
        return labels;
    }
}
