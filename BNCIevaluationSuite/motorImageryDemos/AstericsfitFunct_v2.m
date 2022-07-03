function Fitness=AstericsfitFunct(SinglvectExtractor,Trials,subj)



%%%this is the fitness function to be used to optimize the projections with
%%%a  GA.
tic
for pop=1:length(SinglvectExtractor(:,1))
    %% FEATURE PROJECTION WITH OVR 
    SinglMatExtractor=reshape(SinglvectExtractor(pop,:),20,100);
    extractor{1}=SinglMatExtractor(1:5,:);
    extractor{2}=SinglMatExtractor(6:10,:);
    extractor{3}=SinglMatExtractor(11:15,:);
    extractor{4}=SinglMatExtractor(16:20,:);
    
    Nclass=length(Trials);
    Perf=zeros(4,1);

    for pr=1:4
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
        [dataTrain,dataTest]=splitDataset(data,[],1);
%         parameters.svm.c=inf;
%         parameters.svm.epsilon=1e-7;
%         parameters.svm.kerneloption=0.3;
%         parameters.svm.kernel='gaussian';
        parameters.lda.cl_type='diagquadratic';
        parameters.lda.priorProb='empirical';
        [ypredTrain,ypredTest,]=starClassify(dataTrain, dataTest,parameters);
        
        %% Performance
        ypredTest=squeeze(ypredTest);
        pred=zeros(size(ypredTest,1),size(ypredTest,2));
        false=find(ypredTest<=0);
        true=find(ypredTest>0);
        pred(true)=1;
        pred(false)=-1;
        
        NrPred=size(ypredTest(:));
        GTtemplate=repmat(dataTest.GT,[1 189])';
        Perf(pr)=(NrPred(1)-sum(eq(pred(:),GTtemplate(:))));
    end
Fitness(pop,1)=sum(Perf);
end
toc