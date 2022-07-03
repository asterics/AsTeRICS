function Fitness=AstericsfitFunct_v2(SinglvectExtractor,Trials)

%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% This function is the fitness function to be used to optimize the 
% projections to the class spaces see XINO classification system TN 146
% The functon is prepared to work in vectorized mode
% 
%----------------------------------------------------------
%Inputs
% SinglvectExtractor: Projection matrices reshaped to a single vector. Ex.
%           4 projection matrices from 100 to 5 dimensions:
%
%           PR1(5x100)
%           PR2(5x100)
%           PR3(5x100)  ==> Matrix 20x100 --> reshape to 2000 x 1
%           PR4(5x100)
%
% Trials:   cell_array{} with length equal to the number of classes.In each
%           cell Trials{i} there are all epochs of class i concatenated in a
%           matrix C x S where C is the dimension of the features (100 in the example) and S the
%           number of samples of all epochs concatenated (e.g. for trials of 478 samples if we have 
%           45 epochs, S=478*45).
%----------------------------------------------------------
%Outputs
% 
% Fitness:  Column vector (IndividualsNumber X 1) with the total number of 
%           missclassified time samples for
%           each individual of the generation
%----------------------------------------------------------
%Dependencies
%
%----------------------------------------------------------
% Version   Date        Author  Changes 
% v1        29/07/10    ICL     
% 
%----------------------------------------------------------
% TO DO.
%
% -The projectionmatrices to optimize right now, project to spaces of
% dimesion 5 (see extractor in line 62:65), it should be writen in a
% generic way with an input m=[m1 m2 m3 m4] indicating the dimensions of
% the spaces where the data are projected.
% -It would be interesting to parallelize the function to run the
% individuals of the populations in different threads
%----------------------------------------------------------
% EX.
% 
% Fitness=AstericsfitFunct(SinglvectExtractor,Trials)
%
%--------------------------------------------------------------

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
        IndTest=[80;62;89;60;61;70;72;76;86;55;75;50;87;52;103;117;93;28;142;163;177;20;116;2;94;137;149;139;134;131;24;101;123;173;100;98;161;114;26;106;34;113;170;130;172;3;21;175;140;164;125;171;166;174;105;126;168;128;136;1;91;162;10;40;43;107;121;31;99;112;32;148;152;108;42;135;143;145;141;7;165;150;167;122;41;133;92;146;147;29;132;17;19;23;8;4;95;151;97;36;39;11;158;110;22;178;111;159;120;119;96;154;16;37;153;129];
        [dataTrain,dataTest]=splitDataset_GA(data,IndTest,1);
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