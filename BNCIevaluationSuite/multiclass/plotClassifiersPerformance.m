function plotClassifiersPerformance(pMeasures)
%given the performance measures structure as delivered by
%adjustBaggingFusion, i.e. in a 10 bagging this parameter will look like
% performMeasures = 
% 
%         TPRtrain: [1x10 double]
%         FPRtrain: [1x10 double]
%          TPRtest: [1x10 double]
%          FPRtest: [1x10 double]
%     TPRtrainFuse: [1x10 double]
%     FPRtrainFuse: [1x10 double]
%      TPRtestFuse: [1x10 double]
%      FPRtestFuse: [1x10 double]
%      TPRtrainSVM: [1x10 double]
%      FPRtrainSVM: [1x10 double]
%       TPRtestSVM: [1x10 double]
%       FPRtestSVM: [1x10 double]
%      TPRtrainKNN: [1x10 double]
%      FPRtrainKNN: [1x10 double]
%       TPRtestKNN: [1x10 double]
%       FPRtestKNN: [1x10 double]
%      TPRtrainLDA: [1x10 double]
%      FPRtrainLDA: [1x10 double]
%       TPRtestLDA: [1x10 double]
%       FPRtestLDA: [1x10 double]
%   TPRtrainBaggMV: 0.9914
%   FPRtrainBaggMV: 0.0086
%    TPRtestBaggMV: 0.9595
%    FPRtestBaggMV: 0.2064
% TPRtrainBaggMean: 0.9914
% FPRtrainBaggMean: 0.0086
%  TPRtestBaggMean: 0.9595
%  FPRtestBaggMean: 0.2064


%this function plots the results of the different
%classifiers in a bar plot with error bars
% EX.
% >> plotClassifiersPerformance(pMeasures)
% 
% ans = 
% 
%       bars: [152.0206 155.0201 157.0201 159.0201]
%     errors: [161.0201 164.0201 167.0201 170.0201]
%      title: 173.0201
%     xlabel: 174.0201
%     ylabel: 175.0201
%     legend: 177.0201
%         ca: 151.0201

figure

barvalues=[pMeasures.TPRtrainBaggMean,pMeasures.TPRtrainBaggMV,mean(pMeasures.TPRtrain),mean(pMeasures.TPRtrainFuse),mean(pMeasures.TPRtrainSVM),mean(pMeasures.TPRtrainKNN),mean(pMeasures.TPRtrainLDA);...
pMeasures.TPRtestBaggMean,pMeasures.TPRtestBaggMV,mean(pMeasures.TPRtest),mean(pMeasures.TPRtestFuse),mean(pMeasures.TPRtestSVM),mean(pMeasures.TPRtestKNN),mean(pMeasures.TPRtestLDA);...
pMeasures.FPRtrainBaggMean,pMeasures.FPRtrainBaggMV,mean(pMeasures.FPRtrain),mean(pMeasures.FPRtrainFuse),mean(pMeasures.FPRtrainSVM),mean(pMeasures.FPRtrainKNN),mean(pMeasures.FPRtrainLDA);...
pMeasures.FPRtestBaggMean,pMeasures.FPRtestBaggMV,mean(pMeasures.FPRtest),mean(pMeasures.FPRtestFuse),mean(pMeasures.FPRtestSVM),mean(pMeasures.FPRtestKNN),mean(pMeasures.FPRtestLDA)];


errors=[0,0,var(pMeasures.TPRtrain),var(pMeasures.TPRtrainFuse),var(pMeasures.TPRtrainSVM),var(pMeasures.TPRtrainKNN),var(pMeasures.TPRtrainLDA);...
0,0,var(pMeasures.TPRtest),var(pMeasures.TPRtestFuse),var(pMeasures.TPRtestSVM),var(pMeasures.TPRtestKNN),var(pMeasures.TPRtestLDA);...
0,0,var(pMeasures.FPRtrain),var(pMeasures.FPRtrainFuse),var(pMeasures.FPRtrainSVM),var(pMeasures.FPRtrainKNN),var(pMeasures.FPRtrainLDA);...
0,0,var(pMeasures.FPRtest),var(pMeasures.FPRtestFuse),var(pMeasures.FPRtestSVM),var(pMeasures.FPRtestKNN),var(pMeasures.FPRtestLDA)];

groupnames= {'TPR train','TPR test','FPR train','FPR test'};

barweb(barvalues, errors, [], groupnames, 'performance rates 10 bagging groups (CFV)', 'rate type', 'rate [0,1]', [], [], {'bagg Mean','bagg MV','decim','fusion','SVM','KNN','LDA'})