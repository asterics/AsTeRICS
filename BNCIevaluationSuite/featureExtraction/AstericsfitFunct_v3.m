function Fitness=AstericsfitFunct(SinglvectExtractor,Trials,subj,analyzedClass,scaleFactor,bitDepth)



%%%this is the fitness function to be used to optimize the projections with
%%%a  GA.

%if function is called only with 3 input arguments then the computation is
%based on real chromosomes, for more with binary ones

tic
Nclass=length(Trials);
for pop=1:length(SinglvectExtractor(:,1))
    individual=SinglvectExtractor(pop,:);
    
    %%%%add for binary chromosomes
    if nargin==6
        individual=coeffBin2coeffFloat(individual,bitDepth,scaleFactor);
    end
    
    %% FEATURE PROJECTION WITH OVR 
    SinglMatExtractor=reshape(individual,5,100);
%    extractor{1}=SinglMatExtractor(1:5,:);
%     extractor{2}=SinglMatExtractor(6:10,:);
%     extractor{3}=SinglMatExtractor(11:15,:);
%     extractor{4}=SinglMatExtractor(16:20,:);
    extractor{analyzedClass}=SinglMatExtractor(1:5,:); %for GA in one-class space, the vector has only 5 dimensions
    
    
    Perf=zeros(Nclass,1);

    for pr=analyzedClass:analyzedClass
        data.features=[];
        data.GT=[];
        for daclas=1:Nclass
            B=Trials{daclas};
            prD=extractor{pr}*B;
            data.features=[data.features prD]; 
            if pr==daclas
                data.GT=[data.GT;ones(size(prD,2)/189,1)];
            else
                data.GT=[data.GT;ones(size(prD,2)/189,1)*-1];
            end
        end
        data.features=reshape(data.features,5,189,[]);
       
        %% CLASSIFIER
        [dataTrain,dataTest]=splitDataset(data,0.95,0);
        %[dataTrain,dataTest]=splitDataset(data,[],1); %original
        
%         parameters.svm.c=inf;
%         parameters.svm.epsilon=1e-7;
%         parameters.svm.kerneloption=0.3;
%         parameters.svm.kernel='gaussian';
        parameters.lda.cl_type='diagquadratic';
        parameters.lda.priorProb='empirical';
        
        %%%test error as fitness
        [ypredTrain,ypredTest,]=starClassify(dataTrain,dataTest,parameters); 
        GTtemplate=repmat(dataTest.GT,[1 189])';
        %train error as fitness
        %[ypredTrain,ypredTest,]=starClassify(dataTrain, dataTrain,parameters);
        %GTtemplate=repmat(dataTrain.GT,[1 189])';

        
        %% Performance
        ypredTest=squeeze(ypredTest);
        pred=zeros(size(ypredTest,1),size(ypredTest,2));
        false=find(ypredTest<=0);
        true=find(ypredTest>0);
        pred(true)=1;
        pred(false)=-1;
        
        NrPred=size(ypredTest(:));
        Perf(pr)=(NrPred(1)-sum(eq(pred(:),GTtemplate(:))));
    end
Fitness(pop,1)=sum(Perf);
end
toc