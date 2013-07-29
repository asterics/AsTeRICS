%matlab routine to go through the folders,
%and call the diferent steps involved in the feature extraction
%procedure
%
% developed by Ivan Cester and Joan Llobera 08
% v.13 we pre-process the data

%tested by ASF 04/08 in aureli desktop. Result memory error:
% ??? Error using ==> file2mat
% Memory Map (MapViewOfFile): Espacio de almacenamiento
% insuficiente para procesar este comando.
% 
% Error in ==> file_array.subsref>multifile2mat at 156
%     val(cc(i)+1:cc(i+1)) =
%     file2mat(obj,int32(1),int32(x(y==i)));
% 
% Error in ==> file_array.subsref>subfun at 87
%     t = multifile2mat(sobj,varargin{:});
% 
% Error in ==> file_array.subsref at 60
%     t = subfun(sobj,args{:});
% 
% Error in ==> main_Asterics_Test1_wl_OVR_v1 at 97
%                                         eval(sprintf('B=WlModdataS%d_test(:,251:end,trials);',s)); 


training=1;
recall=1;

Nclass=4;
feature_dirs={'features subject k3b/','features subject k6b/','features subject l1b/'};
load('coord_BCIcomp3-3b.mat');
subj=1;

for m1=4
    for m2=4
        for m3=3
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
                                    eval(sprintf('new_trial=WlModdataS%dC%d(:,251:end,j)'';',s,i));
                                    if(isfinite(sum(sum(new_trial)))) %there are no infinite numbers
                                        mat_data{i}=cat(1,mat_data{i},new_trial);
                                    end
                                end
                                %mean_data{i}=mean(mat_data,3);
                                clear data
                            end
                            pack
                            %disp(sprintf('OVRTraining for subject %d in folder %s', s, subjects{s}));
                            %if we wanted a training specific for each subject:
                            %extractor=OVRtrain_v3(mean_data);
                            %%%%%%%%%%%%%%%%%%%changed asf 03/08
                            %[extractor]=OVRtrain_v5(mat_data,m);
                            [extractor]=OVRtrain_v6(mat_data,m);
                            %%%%%%%%%%%%%%%%%%%
                            save(sprintf('%sfeature_parameters.mat',feature_dirs{s}),'extractor');
                            %save(sprintf('//skat/Projects/UCONTROL-P20070527-01/BCI competition iii/OVR_aproach/%sfeature_training.mat',feature_dirs{s}),'dataset_filt');
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
                                    eval(sprintf('B=WlModdataS%dC%d(:,251:end,l);',s,i));
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
                                        eval(sprintf('B=WlModdataS%d_test(:,251:end,trials);',s));
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