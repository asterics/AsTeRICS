% This script launches a GA to optimize the projection of the features 
% before introducing them to the multi-classifier.
% Te features are loaded from the folders as they are recorded by
% main_Asterics_Test2_wl_anova_GA_v1.m, so ithas to be run to
% calculate the extractor dimension and the initial population
% before running this script
%
% developed by Ivan Cester 2010
% v.1



subj=1;
feature_dirs={'features subject k3b/','features subject k6b/','features subject l1b/'};
subjects={'k3b','k6b','l1b'};

load(sprintf(['WldataS%d.mat'],subj));%we load the data
load(sprintf(['%s_test_GT_reject.mat'],subjects{subj}))% we load the ground truth for the test
load (['WlmoduleS',int2str(subj),'_P.mat']);
NerCoefs=100;

%%%COEFFICIENTS SELECTION AND TRIAL PREPARATION%%%%
% the same coefficients will be chosen for all samples. The criteria to 
% select the coeeficients takes into account the maximum 
%stdP=std(p(:,250:end),0,2);
sumP=sum(p(:,250:end),2);
[pVal,ind]=sort(sumP);
coef=ind(1:NerCoefs);

for clas=1:4
    eval(['len=WlModdataS',int2str(subj),'C',int2str(clas),'.dim(3);']);
    Trials{clas}=[];
    for tr=1:len
        eval(sprintf('Trials{clas}=[Trials{clas} WlModdataS1C%d(coef,250:end,tr)];',clas));
    end
end


%%%%INITIAL POPULATION
load(sprintf('%sfeature_parameters.mat',feature_dirs{subj})); %we load the extractor
SinglMatExtractor=[extractor{1};extractor{2};extractor{3};extractor{4}];
nelem=size(SinglMatExtractor,1)*size(SinglMatExtractor,2); % this is the number of elements to optimize
SinglvectExtractor=reshape(SinglMatExtractor,nelem,1);
%SinglMatExtractor=reshape(SinglvectExtractor,20,100);

noise = wgn(49,nelem,1)/200;
new_population=repmat(SinglvectExtractor',[49 1])+noise;
InitialPop=[SinglvectExtractor';new_population];

options=gaoptimset('InitialPopulation',InitialPop,'PopulationSize',length(InitialPop(:,1)),'PlotInterval',5,'vectorize','on','Generations',120);
tic;[OptimSinglvectExtractor,fval,exitflag,output,population]=ga({@AstericsfitFunct_v2 Trials subj},nelem,[],[],[],[],[],[],[],options);total_GA=toc
save OptimizedProjection_1.mat OptimSinglvectExtractor 



