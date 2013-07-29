%The followinf script preprocessess and extrcats the Wavelet coefficients
%for the BCI competition iii data set 3a.

%TODOs: 1) test the function after Ivan leaving
%       2) make it a self-contained function by suppressing disk access

%
% the steps involved are the following:
%   1-baseline extraction and decimation
%   2-reject epoch with NaN's
%   3-reference to mean all channels
%   4-reject epoch by thresholding
%   5-save data in a file array dataSx.mat will load the file array structure to the workspace (x is the number of subjects)
%   -------
%   6-Extract Time frequency using wavelets (1-30 Hz -->30 coefs for each
%   channel) the baseline. A baseline is subtracted to each TF epoch. The
%   baseline used is the mean of the coefficients of the section of the epoch 
%   previous to the trigger (samples 1:1000 see description of the dataset)
%   7-record the wavelet coefficients in a file array WldataSx.mat will
%   load the file array structure to the workspace (x is the number of subjects)
%   ------
%   8-Calculates an Anova to find the most statistical significant of the 
%   difference between classes of the coefficients.
%
%
%dependencies: 
%SPM toolbox required to build the file_arrays
%
%Wavelet is from wavelet toolbox.

%developed by Ivan Cester Starlab s.l.
%changes:
%

%%%%%data formating%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%Starblast
%    data_dir='/Volumes/Projects-1/S1-ONGOING-PROJECTS/UCONTROL-P20070527-01/BCI competition iii/data set IIIa/cutted in epochs/';
%%%windows
%     data_dir='\\skat\Projects\S1-ONGOING-PROJECTS\UCONTROL-P20070527-01\BCI competition iii\data set IIIa\cutted in epochs\';
%%%%aureli desktop
    data_dir='Z:\S1-ONGOING-PROJECTS\UCONTROL-P20070527-01\BCI competition iii\data set IIIa\cutted in epochs\';
    subjects={'subject k3b/','subject k6b/','subject l1b/'};
    file_names={'class_epoch1.mat','class_epoch2.mat','class_epoch3.mat','class_epoch4.mat'};
    GT={'k3b_test_GT','k6b_test_GT','l1b_test_GT'}
    Nclass=4;
    dec=4;%decimation coefficient
    thres=100;%threshold for rejection (all epochs with amplitude out of [-thres thres] will be rejected)
    basln=floor(1000/dec);
    subj=1 %subjects you want to extract featurees from [1 2 3]
   
    for s=subj  %%%this has been changed by aureli 2/8; original was s=subjects
        %%%trainning data
        for i=1:Nclass
                input_file_name=[data_dir,subjects{s},file_names{i}];
                load(input_file_name);    %this loads the structure class_epoch
                %1-Decimate 250--->125
                class_epoch=dataBCIiii_LTpreproT3(class_epoch,dec);%%decimate
                %2-Reformat           
                data=[];
                for trial=1:length(class_epoch)
                    if sum(sum(isnan(class_epoch{trial})))==0
                        data=cat(3,data,class_epoch{trial}');
                    end
                end
                clear class_epoch
                %3-Reference to the mean of all channels 
                for trial=1:length(data(1,1,:))
                    Ref=mean(data(:,:,trial),1);
                    data(:,:,trial)=data(:,:,trial)-repmat(Ref,[60 1]);
                end
                %4-reject all trials with artefact +-thres uV

                reject=[];
                for trial=1:length(data(1,1,:))
                    if max(max(abs(data(:,:,trial))))>=thres
                        reject=[reject trial]
                    end
                end
                data(:,:,reject)=[];
                        
                    
                
            eval(sprintf('dataS%dC%d=file_array(''dataS%dC%d.dat'',[length(data(:,1,1)) length(data(1,:,1)) length(data(1,1,:))],''FLOAT32-BE'',0,1,0,''rw'');',s,i,s,i));
            eval(sprintf('dataS%dC%d(:,:,:)=data;',s,i));
        end
        
        %%test data
        input_file_name=[data_dir,subjects{s},'class_epoch_test.mat'];
        load(input_file_name);
        load (GT{s})
        
        
        %1-Decimate
        class_epoch=dataBCIiii_LTpreproT3(class_epoch_test,dec);%%decimate
        %2-Reformat           
        data=[];
        rejectNaN=[];
        for trial=1:length(class_epoch)
            if sum(sum(isnan(class_epoch{trial})))==0
                data=cat(3,data,class_epoch{trial}');
            else
                rejectNaN=[rejectNaN trial];
            end
        end
        test_GT(rejectNaN)=[];
        clear class_epoch class_epoch_test
        %3-Reference to the mean of all channels 
        for trial=1:length(data(1,1,:))
            Ref=mean(data(:,:,trial),1);
            data(:,:,trial)=data(:,:,trial)-repmat(Ref,[60 1]);
        end
        %4-reject all trials with artefact +-50uV
        
        reject=[];
        for trial=1:length(data(1,1,:))
            if max(max(abs(data(:,:,trial))))>=thres
                reject=[reject trial]
            end
        end
        data(:,:,reject)=[];
         data_test=data;

        eval(sprintf('dataS%d_test=file_array(''dataS%d_test.dat'',[length(data_test(:,1,1)) length(data_test(1,:,1)) length(data_test(1,1,:))],''FLOAT32-BE'',0,1,0,''rw'');',s,s));
        eval(sprintf('dataS%d_test(:,:,:)=data_test;',s));
        %reject the same trials from the ground truth
        test_GT([reject])=[];
        save ([GT{s},'_reject'],'test_GT')
        %%%%%%%%
        clear data class_epoch class_epoch_test input_file
        eval(sprintf('save dataS%d.mat dataS%d* test_GT',s,s));
    end

    
%%%Feature extraction%%%%%%%%
%%%T-f wavelets coeficients%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 

for sub=1    
    Nclass=4;
    eval(sprintf('load dataS%d.mat',sub))

    srate=250/dec;
    freq=1:1:30;
    S=srate*1.5./freq;
    %SAlpha=S(9:12);

    Ncoefs=1800;%length(dataS1_test(:,1,1))*length(freq);%%this is the number of coeficients we will have
    
    %%%trainning data%%%
    for cl=1:Nclass%n# of classes
        eval(sprintf('WlModdataS%dC%d=file_array(''WlModdataS%dC%d.dat'',[Ncoefs length(dataS%d_test(1,:,1)) length(dataS%dC%d(1,1,:))],''FLOAT32-BE'',0,1,0,''rw'');',sub,cl,sub,cl,sub,sub,cl));
  %      eval(sprintf('WlPhadataS%dC%d=file_array(''WlPhadataS%dC%d.dat'',[Ncoefs length(dataS%d_test(1,:,1)) length(dataS2C1(1,1,:))],''FLOAT32-BE'',0,1,0,''rw'');',sub,cl,sub,cl));
        eval(sprintf('lentr=length(dataS%dC%d(1,1,:));',sub,cl))
        for tr=1:lentr
            count=0;
            for ch=1:60
                eval(sprintf('a=dataS%dC%d(ch,:,tr);',sub,cl))
                b=[ones(100,1)*a(1);a';ones(100,1)*a(end)];
                %cwt is a function of wavelet toolbox for extracting
                %wavelets. cmor1-1.5 is the employed Morlet wavelet.
                wlc=cwt(b,S,'cmor1-1.5');
                wlc=wlc(:,101:end-100);              
                %%%%extract module and phase%%%%baseline is the mean of the first part of the trial (see RWD wavelet analysis case 1)%%%%%
                module=abs(wlc);
                Nmodule=module;
%               
                %extract base line case 2 (see RWD wavelet analysis)
                %module=module-repmat((mean(module(:,1:125),2)),[1 438]);
                %%%normalize module
                 for i=1:length(freq)
                     Nmodule(i,:)=module(i,:)-repmat(mean(module(i,1:basln)),[1 length(module(1,:))]);
                     Nmodule(i,:)=Nmodule(i,:)/std(Nmodule(i,basln+1:end));
                 end
                %%%%%%
                
%               phase = atan2(imag(wlc),real(wlc));
                eval(sprintf('WlModdataS%dC%d(1+count:count+length(freq),:,tr)=Nmodule;',sub,cl))
%                eval(sprintf('WlPhadataS%dC%d(1+count:count+length(freq),:,tr)=phase;',s,cl))
                count=count+length(freq);
                clear a b module Nmodule
            end
        end   
    end


    %%%test data

    eval(sprintf('Ntest=length(dataS%d_test(1,1,:));',sub)) %#n of trials for testing
    eval(sprintf('WlModdataS%d_test=file_array(''WlModdataS%d_test.dat'',[Ncoefs length(dataS%d_test(1,:,1)) Ntest],''FLOAT32-BE'',0,1,0,''rw'');',sub,sub,sub))
 %   WlPhadataS1_test=file_array('WlPhadataS1_test.dat',[Ncoefs length(dataS1_test(1,:,1)) Ntest],'FLOAT32-BE',0,1,0,'rw');
    for tr=1:Ntest
        count=0;
        for ch=1:60%length(dataS1C1(:,1,1))
            eval(sprintf('a=dataS%d_test(ch,:,tr);',sub))
            b=[ones(100,1)*a(1);a';ones(100,1)*a(end)];
            wlc=cwt(b,S,'cmor1-1.5');
            wlc=wlc(:,101:end-100);              
            %%%%extract module and phase%%%%baseline is the mean of the first part of the trial (see RWD wavelet analysis case 1)%%%%%
            module=abs(wlc);
            Nmodule=module;
            
            %extract base line case 2 (see RWD wavelet analysis)
            %module=module-repmat((mean(module(:,1:125),2)),[1 438]);
            %%%normalize module
            for i=1:length(freq)
                Nmodule(i,:)=module(i,:)-repmat(mean(module(i,1:basln)),[1 length(module(1,:))]);
                Nmodule(i,:)=Nmodule(i,:)/std(Nmodule(i,basln+1:end));
            end    
            %%%%%

%            phase = atan2(imag(wlc),real(wlc));
            eval(sprintf('WlModdataS%d_test(1+count:count+length(freq),:,tr)=Nmodule;',sub))
%            eval(sprintf('WlPhadataS%d_test(1+count:count+length(freq),:,tr)=phase;',sub))
            count=count+length(freq);
            clear a b module Nmodule
        end
    end   
    eval(sprintf('save([''WldataS%d.mat''],''Wl*'')',sub))
 end
 

%%%%find most significant coefficiens%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

load WldataS1.mat

%%%we analyse the variance of the module%%%%%%%
p=sparse(length(WlModdataS1_test),length(WlModdataS1C1(1,:,1)))
for s=1:length(WlModdataS1_test)%n# of coefficients of the vector
    dat1=squeeze(WlModdataS1C1(s,:,:));
    dat2=squeeze(WlModdataS1C2(s,:,:));
    dat3=squeeze(WlModdataS1C3(s,:,:));
    dat4=squeeze(WlModdataS1C4(s,:,:));
    clear X
    
    for c=1:length(WlModdataS1C1(1,:,1))
        X=squeeze([dat1(c,:),dat2(c,:),dat3(c,:),dat4(c,:)]);
        group=[ones(length(dat1(c,:)),1);ones(length(dat2(c,:)),1)*2;ones(length(dat3(c,:)),1)*3;ones(length(dat4(c,:)),1)*4];
        p(s,c)=anova1(X,group,'off');
    end
    s
end
save WlmoduleS1P.mat p

figure
imagesc(p)


% load WldataS2.mat
%
%%%%we analyse the variance of the module%%%%%%%
% for s=1:length(WlModdataS2_test)%n# of coefficients of the vector
%     dat1=squeeze(WlModdataS2C1(s,:,:));
%     dat2=squeeze(WlModdataS2C2(s,:,:));
%     dat3=squeeze(WlModdataS2C3(s,:,:));
%     dat4=squeeze(WlModdataS2C4(s,:,:));
%     clear X
%     for c=1:length(WlModdataS2C1(1,:,1))
%         X{1}=squeeze([dat1(c,:),dat2(c,:),dat3(c,:),dat4(c,:)]);
%         X{2}=squeeze([dat2(c,:),dat1(c,:),dat3(c,:),dat4(c,:)]);
%         X{3}=squeeze([dat3(c,:),dat2(c,:),dat1(c,:),dat4(c,:)]);
%         X{4}=squeeze([dat4(c,:),dat2(c,:),dat3(c,:),dat1(c,:)]);        
%         for classcomb=1:4 %1-12,2-13,3-14,4-23,5-24,6-34
%             p(s,c,classcomb)=anova1(X{classcomb},[ones(1,length(dat1(c,:))),2*ones(1,3*length(dat1(c,:)))],'off');
%         end
%     end
%     s
% end
% save WlmoduleS2P4.mat p
% 
% figure
% for pl=1:4
% subplot(2,2,pl)
% imagesc(p(:,:,pl))
% title(['p for class comb ',int2str(pl)])
% end
% 
% load WldataS3.mat
% 
% %%%%we analyse the variance of the module%%%%%%%
% for s=1:length(WlModdataS3_test)%n# of coefficients of the vector
%     dat1=squeeze(WlModdataS3C1(s,:,:));
%     dat2=squeeze(WlModdataS3C2(s,:,:));
%     dat3=squeeze(WlModdataS3C3(s,:,:));
%     dat4=squeeze(WlModdataS3C4(s,:,:));
%     clear X
%     for c=1:length(WlModdataS3C1(1,:,1))
%         X{1}=squeeze([dat1(c,:),dat2(c,:),dat3(c,:),dat4(c,:)]);
%         X{2}=squeeze([dat2(c,:),dat1(c,:),dat3(c,:),dat4(c,:)]);
%         X{3}=squeeze([dat3(c,:),dat2(c,:),dat1(c,:),dat4(c,:)]);
%         X{4}=squeeze([dat4(c,:),dat2(c,:),dat3(c,:),dat1(c,:)]);        
%         for classcomb=1:4 %1-12,2-13,3-14,4-23,5-24,6-34
%             p(s,c,classcomb)=anova1(X{classcomb},[ones(1,length(dat1(c,:))),2*ones(1,3*length(dat1(c,:)))],'off');
%         end
%     end
%     s
% end
% save WlmoduleS3P4.mat p
% 
% figure
% for pl=1:4
% subplot(2,2,pl)
% imagesc(p(:,:,pl))
% title(['p for class comb ',int2str(pl)])
% end