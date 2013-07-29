function InvPerf=AstericsfitFunct(SinglvectExtractor)


tic
%%this is the fitness function to be used to optimize the projections with
%%a  GA.

subj=1;
Nclass=4;
feature_dirs={'features subject k3b/','features subject k6b/','features subject l1b/'};
subjects={'k3b','k6b','l1b'};

load(sprintf(['WldataS%d.mat'],subj));%we load the data
load(sprintf(['%s_test_GT_reject.mat'],subjects{subj}))% we load the ground truth for the test
load (['WlmoduleS',int2str(subj),'_P.mat']);
NerCoefs=100;

%%%COEFFICIENTS SELECTION 2%%%%
% the same coefficients will be chosen for all samples. The criteria to 
% select the coeeficients takes into account the maximum 
stdP=std(p(:,250:end),0,2);
sumP=sum(p(:,250:end),2);
[pVal,ind]=sort(sumP);
coef=ind(1:NerCoefs);



SinglMatExtractor=reshape(SinglvectExtractor,20,100);

extractor{1}=SinglMatExtractor(1:5,:);
extractor{2}=SinglMatExtractor(6:10,:);
extractor{3}=SinglMatExtractor(11:15,:);
extractor{4}=SinglMatExtractor(16:20,:);

%% FEATURE PROJECTION WITH OVR                                                   
%%% we extract the features for the training set
                    

for i=1:Nclass
    eval(['len=WlModdataS',int2str(subj),'C',int2str(i),'.dim(3);']);
    for l=1:len
        for j=1:Nclass
            A=extractor{j};
            eval(sprintf('B=WlModdataS%dC%d(coef,250:end,l);',subj,i));
            Z{j}{l}=A*B; %for each class and trial, we have a 750*60 matrix
        end
    end
    feats_train{i}=Z;
    %   save(sprintf('%sfeatures_class%d.mat',feature_dirs{subj},i),'Z');
    clear Z
end
                 
%%% we extract the features for the test set

                   
for clas=1:Nclass     
    
    tr=find(test_GT==clas)';
    count=1;
    for trials=tr
        for j=1:Nclass
            A=extractor{j};
            eval(sprintf('B=WlModdataS%d_test(coef,250:end,trials);',subj));
            Z{j}{count}=A*B; %for each class and trial, we have a 750*60 matrix                                
        end
        count=count+1;
    end
    feats_test{clas}=Z;
%    save(sprintf('%srecall_features_s%d_c%d.mat',feature_dirs{subj},subj,clas),'Z');
    clear Z

 end
                  

%% reformating features for the multiclassifier format

   
%we want to go from a structure of i files with i2 cells to another
%of i2 files with i cells
for i2=1:Nclass
    disp(sprintf('Reformatting the data for classifier of class %d',i2));
    data.features=[];
    data.GT=[];
    for i=1:Nclass
        Z=feats_train{i}; %load(sprintf('%sfeatures_class%d.mat',feature_dir,i)); %this load Z{class_cand}{trial}(samples, channels)
        Ntrials=length(Z{i});
        for l=1:Ntrials
            temp(:,:,l)=Z{i2}{l};
            Z{i2}{l}=[];
            %re4mat{i}.features=temp;
            if(i2==i)
                %re4mat{i2}.GT(l)=1;
                GT(l)=1;
            else
                %re4mat{i2}.GT(l)=0;
                GT(l)=0;
            end
        end
        %cat(3,data.features,temp);                    
        %cat(1,data.GT,GT);
        %data=[data, re4mat{i2}];
        data.GT=[data.GT, GT];
        %data.features=[data.features, temp];
        data.features=cat(3,data.features, temp);
        clear Z temp GT;
    end

    
    dataTrain(i2)=data;
    %save(sprintf('%sfeatures4aproach1_c%d.mat',feature_dir,i2),'data');
    %we are saving, for each class, the candidate "feature" from
    %all the data set, with an associated ground truth
    %this is:
    %data.features is matrix of size "channels X samples X trials"
    %data.gt is ground truth with length "trials"
end

%%%%TEST SET
%we want to go from a structure of i files with i2 cells to another
%of i2 files with i cells
for i2=1:Nclass
    data.features=[];
    data.GT=[];
    for i=1:Nclass
        Z=feats_test{i}; %load(sprintf('%srecall_features_s%d_c%d.mat',feature_dir,s,i)); %this load Z{class_cand}{trial}(samples, channels)
        Ntrials=length(Z{i});
        for l=1:Ntrials
            temp(:,:,l)=Z{i2}{l};
            Z{i2}{l}=[];
            %re4mat{i}.features=temp;
            if(i2==i)
                %re4mat{i2}.GT(l)=1;
                GT(l)=1;
            else
                %re4mat{i2}.GT(l)=0;
                GT(l)=0;
            end
        end
        %cat(3,data.features,temp);                    
        %cat(1,data.GT,GT);
        %data=[data, re4mat{i2}];
        data.GT=[data.GT, GT];
        %data.features=[data.features, temp];
        data.features=cat(3,data.features, temp);
        clear Z temp GT;
    end
    
    dataTest(i2)=data;
    %disp(sprintf('saving %sfeatures4aproach1_c%d.mat',feature_dirs{s},i2));
    %save(sprintf('%stest_features4aproach1_c%d.mat',feature_dir,i2),'data');
    %we are saving, for each class, the candidate "feature" from
    %all the data set, with an associated ground truth
    %this is:
    %data.features is matrix of size "channels X samples X trials"
    %data.gt is ground truth with length "trials"
end

toc

%% MULTI-CLASSIFIER

[kap,kapTest,classLabelSamples,classLabelSamplesTest,classPrediction,classPredictionTest]=bciOVRframework(dataTrain,dataTest,2);

InvPerf=1/kapTest(2)
if kapTest(2) > 0.38
    save current_optimized_ProjMat.mat SinglvectExtractor
end