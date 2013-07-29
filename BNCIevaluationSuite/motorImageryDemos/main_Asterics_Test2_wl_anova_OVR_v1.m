%matlab routine to go through the folders,
%and call the diferent steps involved in the feature extraction
%procedure
%
% developed by Ivan Cester and Joan Llobera 08
% v.13 we pre-process the data

%%%tested by asf 04/08 on aureli's desktop. Result error:
% ??? Undefined function or method 'svmclass' for input
% arguments of type 'double'.
% 
% Error in ==> starClassify at 220
%             [xsup,w,b,pos]=svmclass(sampleData,yapp,c,epsilon,kernel,kerneloption,verbose);
%             
% Error in ==> baggingFusion at 98
%     [yTrain4Fusion,yDataStar]=starClassify(xTrain,testDataAll);
%     %data is only used for recalling
% 
% Error in ==> bciOVRframework at 43
%         [trainBaggingPrediction,testBaggingPrediction]=baggingFusion(xTrain(k),xTest(k),baggingRuns,homogeneousBagging);
%         
% Error in ==> main_Asterics_Test2_wl_anova_OVR_v1 at 136
%                 [kap,kapTest,classLabelSamples,classLabelSamplesTest,classPrediction,classPredictionTest]=bciOVRframework(dataTrain,dataTest,2);


training=1;
recall=1;

Nclass=4;
feature_dirs={'features subject k3b/','features subject k6b/','features subject l1b/'};
load('coord_BCIcomp3-3b.mat');
subj=1;

%% %%we first select the most significant coefficients with the result of
%%%%the anova%%%%%%%%%%%%

load (['WlmoduleS',int2str(subj),'_P.mat']);
NerCoefs=100;

%%%COEFFICIENTS SELECTION 1%%%%
% for each time sample the NerCoefs coefficients with lower p are selected
% coef=zeros(NerCoefs,length(p(1,:)));
% for i=1:length(p(1,:))
%     coefs_sampl=p(:,i);
%     for j=1:NerCoefs
%         [m,ind]=min(coefs_sampl);
%         coef(j,i)=ind(1);
%         coefs_sampl(ind)=[];
%         
%     end
% end

%%%COEFFICIENTS SELECTION 2%%%%
% the same coefficients will be chosen for all samples. The criteria to 
% select the coeeficients takes into account the maximum 
stdP=std(p(:,250:end),0,2);
sumP=sum(p(:,250:end),2);
[pVal,ind]=sort(sumP);
coef=ind(1:NerCoefs);
%% %%%%%%%%%%%%%%%%

for m1=5
    for m2=5
        for m3=5
            for m4=5
                m=[m1 m2 m3 m4]
                if training==1
                    data_dir='data/data Asterics/';     
                    
                    %% %now, we train for the features (OVR)
                    for s=subj%length(subjects)!!!!
                        %we  check we don't need to train the system
                        fid=-1;
                        if fid==-1 %there is no such file
                            mkdir([feature_dirs{s}]);
                            %disp(sprintf('Now preparing data for OVRTraining for subject %d in folder %s',  s, subjects{s}));
                            load(sprintf(['WldataS%d.mat'],s));
                            for i=1:Nclass
                                %mean_data{i}=zeros(size(data.trial{1}));
                                mat_data{i}=[];
                                eval(['len=WlModdataS',int2str(s),'C',int2str(i),'.dim(3);']);
                                for j=1:len
                                    eval(sprintf('new_trial=WlModdataS%dC%d(coef,250:end,j)'';',s,i));                                    
                                    if(isfinite(sum(sum(new_trial)))) %there are no infinite numbers
                                        mat_data{i}=cat(1,mat_data{i},new_trial);
                                    end
                                end
                                %mean_data{i}=mean(mat_data,3);
                                clear data new_trial
                            end
                            pack
                            %disp(sprintf('OVRTraining for subject %d in folder %s', s, subjects{s}));
                            %if we wanted a training specific for each subject:
                            %extractor=OVRtrain_v3(mean_data);
                            [extractor]=OVRtrain_v6(mat_data,m);
                            save(sprintf('%sfeature_parameters.mat',feature_dirs{s}),'extractor');
                            %save(sprintf('//skat/Projects/UCONTROL-P20070527-01/BCI competition iii/OVR_aproach/%sfeature_training.mat',feature_dirs{s}),'dataset_filt');
                            clear mat_data
                        end
                    end

                    %we extract the features for the training set (for the classification training):

                    for s=subj%length(subjects)!!!!!!
                        load(sprintf('%sfeature_parameters.mat',feature_dirs{s})); %we load the extractor
                        %disp(sprintf('Now extracting features of Training Set for subject %d in folder %s',s,subjects{s}));
                        load(sprintf(['WldataS%d.mat'],s));%we load the data
                        for i=1:Nclass
                            eval(['len=WlModdataS',int2str(s),'C',int2str(i),'.dim(3);']);
                            for l=1:len
                                for j=1:Nclass
                                    A=extractor{j};
                                    eval(sprintf('B=WlModdataS%dC%d(coef,250:end,l);',s,i));
                                    Z{j}{l}=A*B; %for each class and trial, we have a 750*60 matrix
                                end
                            end
                            save(sprintf('%sfeatures_class%d.mat',feature_dirs{s},i),'Z');
                            clear Z
                        end
                    end
                   
                end

                if recall==1
                    data_dir='data/data Asterics/';
                    subjects={'k3b','k6b','l1b'};
                    %features extraction:.............................................
                    for s=subj%length(subjects)
                        load(sprintf(['WldataS%d.mat'],s));%we load the data
                        load(sprintf('%sfeature_parameters.mat',feature_dirs{s}));%%%%
                        load(sprintf(['%s_test_GT_reject.mat'],subjects{s}))
                        for clas=1:Nclass     
                            fid=-1;
                            if fid==-1
                                tr=find(test_GT==clas)';
                                count=1;
                                for trials=tr
                                    for j=1:Nclass
                                        A=extractor{j};
                                        eval(sprintf('B=WlModdataS%d_test(coef,250:end,trials);',s));
                                        Z{j}{count}=A*B; %for each class and trial, we have a 750*60 matrix                                
                                    end
                                    count=count+1;
                                end
                                save(sprintf('%srecall_features_s%d_c%d.mat',feature_dirs{s},s,clas),'Z');
                                clear Z
                            end
                        end
                   end

                end
                reformat4classification(feature_dirs{subj},subj)
                [dataTrain,dataTest]=openFiles(feature_dirs{subj})
                [kap,kapTest,classLabelSamples,classLabelSamplesTest,classPrediction,classPredictionTest]=bciOVRframework(dataTrain,dataTest,2);
                kapMajTrain(m1,m2,m3,m4)=kap(1);
                kapAveTrain(m1,m2,m3,m4)=kap(2)
                kapMajTest(m1,m2,m3,m4)=kapTest(1);
                kapAveTest(m1,m2,m3,m4)=kapTest(2)                
                toc
            end
        end
    end
end
%save ('resultsMtune\KappaTuningMtestSoA.mat','kapMajTrain','kapAveTrain','kapMajTest','kapAveTest')