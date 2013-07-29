    %matlab routine to go through the folders,
%and call the diferent steps involved in the feature extraction
%procedure
%
% developed by Ivan Cester and Joan Llobera 08
% v.13 we pre-process the data
%
% note that in the paths //Phact has been replaced by /Volumes to run in
% Starblast. A search and replace in the other direction should have the
% same effect
% v2.try with OVR transposing the data and local data folders
% v3.data stored locally and take care of NaN?s in the filtered data
% v4. we use the data v2 ei. no NaN?s and no artefacts(since BCI comp HDR)
% in any epoch. The test data is also separated in different files for each
% class.

%%%%tested asf 04/08 on aureli's desktop with error result:
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
% Error in ==> main_TestDeciimationTPR112_v4_SoA at 220
%                 [kap,kapTest,classLabelSamples,classLabelSamplesTest,classPrediction,classPredictionTest]=bciOVRframework(dataTrain,dataTest,2); 


training=1;
recall=1;
if training==0;
    recall=1;
end

Nclass=4;
feature_dirs={'features subject k3b/','features subject k6b/','features subject l1b/'};
load('coord_BCIcomp3-3b.mat');
subj=1;

for m1=5:2:11
    for m2=15
        for m3=15
            for m4=15
                m=[m1 m2 m3 m4]
                if training==1
                    tic
                    subjects={'raw subject k3b/','raw subject k6b/','raw subject l1b/'};
                    %subjects={'debugg/'};
                    file_names={'v3class_epoch1.mat','v3class_epoch2.mat','v3class_epoch3.mat','v3class_epoch4.mat'};
                    %file_names={'debugg1.mat','debugg2.mat','debugg3.mat','debugg4.mat'};
                    addpath(genpath('\\phact\code\02_-_Project_Related_Code\U_CONTROL\toolboxes_required\MatLab_EEG_Toolkit\eeglab4_4/eeglab4.4b'));
                    %first, we filter: laplace and Beta filtering.
                    for s=subj %length(subjects)!!!!!
                        for i=1:Nclass
                            fid=1;
                            if fid==-1 %we  check we haven't processed this
                                input_file_name=[subjects{s},file_names{i}];
                                load(input_file_name);    %this loads the structure class_epoch
                                class_epoch=dataBCIiii_LTpreproT3(class_epoch,1);%%%%original uses dataBCIiii_prepro without decimation
                                n_elec=length(class_epoch{1}(1,:));
                                %disp(sprintf('Now Filtering for subject %d in folder %s and class %d', s, subjects{s},i));
                                clear raw_data
                                for the_epoch=1:length(class_epoch)
                                    the_epoch
                                    raw_data=class_epoch{the_epoch};  %raw_data has size: samples x channels
                                    %here we look for NaN?s in the data and we substitue
                                    %them with the previous value in the same channel.
                                    bad=isnan(raw_data);
                                    if sum(sum(bad))~=0
                                        f=find(bad==1);
                                        raw_data(f)=raw_data(f-1);
                                    end

                                    %we will need the function in the folder and subfolders:
                                    %           laplace_filter
                                    data2=U_C_lap_perrin(raw_data(250:end,:),channel_location,n_elec);
                                    bad=isnan(data2);
                                    if sum(sum(bad))~=0
                                        f=find(bad==1);
                                        data2(f)=data2(f-1);
                                    end
                                    % we extract beta activity
%                                     data3=filterBeta(data2); Filter is designed for 20 hz sampling rate. In
%                                                                   this case Sampling rate is 62.5 so
%                                                                   max frequency si already about 30 Hz
                                    data.trial{the_epoch}=data2;
                                    data.class{the_epoch}=i;
                                    clear('raw_data','data2','data3');
                                end
                                save(sprintf('filtered_data_c%d_s%d.mat',i,s),'data');
                                %        note there is redundancy in labelling the data with class number
                                clear class_epoch data
                            end
                        end
                    end
                   

                    %now, we train for the features (OVR)

                %% %
                    for s=subj%length(subjects)!!!!
                        %we  check we don't need to train the system
                        fid=-1;
                        if fid==-1 %there is no such file
                            mkdir([feature_dirs{s}]);
                            %disp(sprintf('Now preparing data for OVRTraining for subject %d in folder %s',  s, subjects{s}));
                            for i=1:Nclass
                                load(sprintf('filtered_data_c%d_s%d.mat',i,s));
                                %mean_data{i}=zeros(size(data.trial{1}));
                                mat_data{i}=[];
                                for j=1:length(data.trial)
                                    if(isfinite(sum(sum(data.trial{j})))) %there are no infinite numbers
                                        mat_data{i}=cat(1,mat_data{i},data.trial{j});
                                    end
                                end
                                %mean_data{i}=mean(mat_data,3);
                                clear data
                            end
                            %disp(sprintf('OVRTraining for subject %d in folder %s', s, subjects{s}));
                            %if we wanted a training specific for each subject:
                            %extractor=OVRtrain_v3(mean_data);
                            [extractor]=OVRtrain_v6(mat_data,m);
                            save(sprintf('%sfeature_parameters.mat',feature_dirs{s}),'extractor');
                            clear mat_data
                            %save(sprintf('//skat/Projects/UCONTROL-P20070527-01/BCI competition iii/OVR_aproach/%sfeature_training.mat',feature_dirs{s}),'dataset_filt');
                        end
                    end

                    %we extract the features for the training set (for the classification training):

                    for s=subj%length(subjects)!!!!!!
                        clear Z
                        load(sprintf('%sfeature_parameters.mat',feature_dirs{s})); %we load the extractor
                        for i=1:Nclass
                            %disp(sprintf('Now extracting features of Training Set for subject %d, and class %d, in folder %s',  s,i, subjects{s}));
                            load(sprintf('filtered_data_c%d_s%d.mat',i,s));%we load the data
                            for l=1:length(data.trial)
                                data.trial{l};
                                for j=1:Nclass
                                    A=extractor{j};
                                    B=data.trial{l}';
                                    Z{j}{l}=A*B; %for each class and trial, we have a 750*60 matrix
                                end
                            end
                            save(sprintf('%sfeatures_class%d.mat',feature_dirs{s},i),'Z');
                            clear Z
                        end
                    end
                   
                end

                if recall==1
                    
                    subjects={'raw subject k3b/','raw subject k6b/','raw subject l1b/'};
                    file_names={'v3class_epoch_test1.mat','v3class_epoch_test2.mat','v3class_epoch_test3.mat','v3class_epoch_test4.mat'};
                    GT_dir='//skat/Projects/UCONTROL-P20070527-01/BCI competition iii/data set IIIa/Ground Truth/';
                    GT_files={'k3b_test_GT.mat','k6b_test_GT.mat','l1b_test_GT.mat'};
                    %assuming the training was done individually for each subject
                    for s=subj%length(subjects)
                       for i=1:Nclass
                            fid=1;
                            if fid==-1 %we  check we haven't processed this
                                input_file_name=[subjects{s},file_names{i}];
                                load(input_file_name);    %this loads the structure class_epoch
                                class_epoch=dataBCIiii_LTpreproT3(class_epoch_test,1);
                                n_elec=length(class_epoch{1}(1,:));
                                %disp(sprintf('Now Filtering for subject %d in folder %s and class %d', s, subjects{s},i));
                                %addpath(genpath('\\phact\code\02_-_Project_Related_Code\U_CONTROL\toolboxes_required\MatLab_EEG_Toolkit\eeglab4_4/eeglab4.4b'));
                                clear data
                                for the_epoch=1:length(class_epoch)
                                    the_epoch
                                    raw_data=class_epoch{the_epoch};  %raw_data has size: samples x channels
                                    %here we look for NaN?s in the data and we substitue
                                    %them with the previous value in the same channel.
                                    bad=isnan(raw_data);
                                    if sum(sum(bad))~=0
                                        f=find(bad==1);
                                        raw_data(f)=raw_data(f-1);
                                    end

                                    %we will need the function in the folder and subfolders:
                                    
                                    % laplace_filter
                                    data2=U_C_lap_perrin(raw_data(250:end,:),channel_location,n_elec);
                                    bad=isnan(data2);
                                    if sum(sum(bad))~=0
                                        f=find(bad==1);
                                        data2(f)=data2(f-1);
                                    end
                                    %         we extract beta activity
                                    %data3=filterBeta(data2);
                                    data.trial{the_epoch}=data2;
                                    data.class{the_epoch}=i;
                                    clear('raw_data','data2','data3');
                                end
                                save(sprintf('recall_filtered_data_c%d_s%d.mat',i,s),'data');
                                %        note there is redundancy in labelling the data with class number
                                clear Z data class_epoch class_epoch_test
                            end
                        end
                    end


                        %we extract the features:.............................................
                   for s=subj%length(subjects)
                        for clas=1:Nclass     
                            %we check we haven't already extracted the features:
                            fid=-1;
                            if fid==-1
                                clear Z
                                load(sprintf('%sfeature_parameters.mat',feature_dirs{s}));%%%%
                                %TO ensure we do not reuse this:
                                clear data;
                                load(sprintf('recall_filtered_data_c%d_s%d.mat',clas,s),'data');%%JL modified             
                                for trials=1:length(data.trial)
                                    data.trial{trials};
                                    for j=1:Nclass
                                        A=extractor{j};
                                        B=data.trial{trials};
                                        Z{j}{trials}=A*B'; %for each class and trial, we have a 750*60 matrix
                                    end
                                end
                                save(sprintf('%srecall_features_s%d_c%d.mat',feature_dirs{s},s,clas),'Z');
                                clear Z
                            end
                        end
                   end

                end
                reformat4classification(subj)
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