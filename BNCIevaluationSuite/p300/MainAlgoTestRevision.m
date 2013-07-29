% This script learn a classifier for each spelling session
% the hyperparameters of each classifier should be save
% in a .mat file
%
%

% 07/07

clear all
close all




data.normalizationtype='normal';
data.typedata='allfilt';
data.highcutofffrequency=20;
h = helpdlg('Select directoy with preprocessed data');
uiwait(h);
DirInfo.pathdata=uigetdir('','Select directoy with preprocessed data');
DirInfo.pathdata=strcat(DirInfo.pathdata,'/');
%DirInfo.pathdata='./preprocesseddata/';

h = helpdlg('Select directory for output data');
uiwait(h);
outputpath = uigetdir('Get','Select directory for output data');
outputpath = strcat(outputpath,'/');

h = helpdlg('Select directory with model data');
uiwait(h);
DirInfo.pathmodelsel=uigetdir('','Select directory with model data');
DirInfo.pathmodelsel=strcat(DirInfo.pathmodelsel,'/');

%%-----------------------------------------
% learning using channel selection parameter
% These are the settings for getting the competition's results
%-------------------------------------------
chanselparam.NbChanMax=4; % final number of channel
chanselparam.RemoveChan=4; % final number of channel
filenote='';
%DirInfo.pathmodelsel=['.\cv' int2str(chanselparam.NbChanMax) filenote '\'];
channelselection=1;   % if channel has been ranked
chanselparam.channelchoice='optimal'; % optimal selection according to criterion 'optimal' or 'adhoc'
chanselparam.nbchannel=30; % number of channel to select if 'adhoc
chanselparam.criterion='tp'; % criterion for channel selection

% %%%-----------------------------------------

% ----------------------------------------
% Uncomment next lines for producing results with ensemble of SVM and  64 channels%
% ----------------------------------------

channelselection=0;
%DirInfo.pathmodelsel=['.\cv64\'];
nbchanvec=64;

% ----------------------------------------
% Uncomment next lines for producing results with ensemble of SVM and  8 channels%
% ----------------------------------------

% channelselection=0;
% DirInfo.pathmodelsel=['.\cv8chanfixed\'];
% nbchanvec=8;

%% ----------------------------------------
%% Learn classifier or uses preprocessed classifiers
%% ----------------------------------------
doclassification=0;

%% ----------------------------------------
%%  Choose the subject
%% ----------------------------------------
donnees = 'B'; %

nbshotvec=[1:15];


switch donnees
    
    case  'A'
        
        File.app={ ...
                'A1-A2-A3-A4-A5'   'A6-A7-A8-A9-A10' ...
                'A11-A12-A13-A14-A15'    'A16-A17-A18-A19-A20' ...
                'A21-A22-A23-A24-A25'        'A26-A27-A28-A29-A30' ...
                'A31-A32-A33-A34-A35'  'A36-A37-A38-A39-A40'  ...
                'A41-A42-A43-A44-A45'  'A46-A47-A48-A49-A50' ...
                'A51-A52-A53-A54-A55'  'A56-A57-A58-A59-A60' ...
                'A61-A62-A63-A64-A65'  'A66-A67-A68-A69-A70' ...
                'A71-A72-A73-A74-A75'  'A76-A77-A78-A79-A80' ...
                'A81-A82-A83-A84-A85' ...%
            };
        filet={...
                'At1' 'At2'  'At3' 'At4' 'At5'  'At6' 'At7' 'At8'  'At9' 'At10' ...
                'At11' 'At12'  'At13' 'At14' 'At15'  'At16' 'At17' 'At18'  'At19' 'At20' ...
                'At21' 'At22'  'At23' 'At24' 'At25'  'At26' 'At27' 'At28'  'At29' 'At30' ...
                'At31' 'At32'  'At33' 'At34' 'At35'  'At36' 'At37' 'At38'  'At39' 'At40' ...
                'At41' 'At42'  'At43' 'At44' 'At45'  'At46' 'At47' 'At48'  'At49' 'At50' ...
                'At51' 'At52'  'At53' 'At54' 'At55'  'At56' 'At57' 'At58'  'At59' 'At70' ...
                'At61' 'At62'  'At63' 'At64' 'At65'  'At66' 'At67' 'At68'  'At69' 'At70' ...
                'At71' 'At72'  'At73' 'At74' 'At75'  'At76' 'At77' 'At78'  'At79' 'At80' ...
                'At81' 'At82'  'At83' 'At84' 'At85'  'At86' 'At87' 'At88'  'At89' 'At90' ...
                'At91' 'At92'  'At93' 'At94' 'At95'  'At96' 'At97' 'At98'  'At99' 'At100' ...
            };
        
        
        
    case 'B'
        File.app={ ...
                
            'B1-B2-B3-B4-B5'  ...
                'B6-B7-B8-B9-B10' ...
                'B11-B12-B13-B14-B15' ...
                'B16-B17-B18-B19-B20' ...
                'B21-B22-B23-B24-B25' ...
                'B26-B27-B28-B29-B30' ...
                'B31-B32-B33-B34-B35'  'B36-B37-B38-B39-B40'  ...
                'B41-B42-B43-B44-B45' ...
                'B46-B47-B48-B49-B50' ...
                'B51-B52-B53-B54-B55'  'B56-B57-B58-B59-B60' ...
                'B61-B62-B63-B64-B65'  'B66-B67-B68-B69-B70' ...
                'B71-B72-B73-B74-B75'  'B76-B77-B78-B79-B80' ...
                'B81-B82-B83-B84-B85' ...%
            };
        filet={...
                'Bt1' 'Bt2'  'Bt3' 'Bt4' 'Bt5'  'Bt6' 'Bt7' 'Bt8'  'Bt9' 'Bt10' ...
                'Bt11' 'Bt12'  'Bt13' 'Bt14' 'Bt15'  'Bt16' 'Bt17' 'Bt18'  'Bt19' 'Bt20' ...
                'Bt21' 'Bt22'  'Bt23' 'Bt24' 'Bt25'  'Bt26' 'Bt27' 'Bt28'  'Bt29' 'Bt30' ...
                'Bt31' 'Bt32'  'Bt33' 'Bt34' 'Bt35'  'Bt36' 'Bt37' 'Bt38'  'Bt39' 'Bt40' ...
                'Bt41' 'Bt42'  'Bt43' 'Bt44' 'Bt45'  'Bt46' 'Bt47' 'Bt48'  'Bt49' 'Bt50' ...
                'Bt51' 'Bt52'  'Bt53' 'Bt54' 'Bt55'  'Bt56' 'Bt57' 'Bt58'  'Bt59' 'Bt70' ...
                'Bt61' 'Bt62'  'Bt63' 'Bt64' 'Bt65'  'Bt66' 'Bt67' 'Bt68'  'Bt69' 'Bt70' ...
                'Bt71' 'Bt72'  'Bt73' 'Bt74' 'Bt75'  'Bt76' 'Bt77' 'Bt78'  'Bt79' 'Bt80' ...
                'Bt81' 'Bt82'  'Bt83' 'Bt84' 'Bt85'  'Bt86' 'Bt87' 'Bt88'  'Bt89' 'Bt90' ...
                'Bt91' 'Bt92'  'Bt93' 'Bt94' 'Bt95'  'Bt96' 'Bt97' 'Bt98'  'Bt99' 'Bt100' ...
            };
        
        
        
        %
end;


testA='WQXPLZCOMRKO97YFZDEZ1DPI9NNVGRQDJCUVRMEUOOOJD2UFYPOO6J7LDGYEGOA5VHNEHBTXOO1TDOILUEE5BFAEEXAW_K4R3MRU';
testB='MERMIROOMUHJPXJOHUVLEORZP3GLOO7AUFDKEFTWEOOALZOP9ROCGZET1Y19EWX65QUYU7NAK_4YCJDVDNGQXODBEV2B5EFDIDNR';

if ~channelselection
    File.save=['EnsembleClassifier-' data.typedata '- ' donnees int2str(nbchanvec) '.mat'];
else
    File.save=['EnsembleClassifier-' data.typedata '- ' donnees  '.mat'];
    
end
File.save = strcat(outputpath,File.save);
if doclassification
    
    createclassifier(DirInfo,File,data,channelselection,chanselparam);
end;
fileclassifier=File.save;
result=TestClassifier(fileclassifier,filet,DirInfo,data,nbshotvec);
%--------------------------------------------------------------------------


for k=1:length(nbshotvec)
    perfA(k)=sum(result(k,:)==testA);
    perfB(k)=sum(result(k,:)==testB);
end;

perfA
perfB
