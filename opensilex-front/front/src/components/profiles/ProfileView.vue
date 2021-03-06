<template>
  <div>
    <b-button @click="showCreateForm" variant="success">Add profile</b-button>
    <opensilex-ProfileForm
      ref="profileForm"
      v-bind:credentialsGroups="credentialsGroups"
      @onCreate="callCreateProfileService"
      @onUpdate="callUpdateProfileService"
    ></opensilex-ProfileForm>
    <opensilex-ProfileList
      ref="profileList"
      v-bind:credentialsGroups="credentialsGroups"
      @onEdit="editProfile"
      @onDelete="deleteProfile"
    ></opensilex-ProfileList>
  </div>
</template>

<script lang="ts">
import { Component } from "vue-property-decorator";
import Vue from "vue";
import {
  ProfileService,
  ProfileCreationDTO,
  ProfileUpdateDTO,
  ProfileGetDTO,
  SecurityService,
  CredentialsGroupDTO
} from "opensilex-rest/index";
import HttpResponse, { OpenSilexResponse } from "opensilex-rest/HttpResponse";

@Component
export default class ProfileView extends Vue {
  $opensilex: any;
  service: ProfileService;
  $store: any;
  credentialsGroups: Array<CredentialsGroupDTO> = [];

  get user() {
    return this.$store.state.user;
  }

  static credentialsGroups = [];
  static async asyncInit($opensilex) {
    console.debug("Loading credentials list...");
    let security: SecurityService = await $opensilex.loadService(
      "opensilex-rest.SecurityService"
    );
    let http: HttpResponse<
      OpenSilexResponse<Array<CredentialsGroupDTO>>
    > = await security.getCredentialsGroups();
    ProfileView.credentialsGroups = http.response.result;
    console.debug("Credentials list loaded !", ProfileView.credentialsGroups);
  }

  created() {
    this.credentialsGroups = ProfileView.credentialsGroups;
    this.service = this.$opensilex.getService("opensilex.ProfileService");
  }

  showCreateForm() {
    let profileForm: any = this.$refs.profileForm;
    profileForm.showCreateForm();
  }

  callCreateProfileService(form: ProfileCreationDTO, done) {
    done(
      this.service
        .createProfile(this.user.getAuthorizationHeader(), form)
        .then((http: HttpResponse<OpenSilexResponse<any>>) => {
          let uri = http.response.result;
          console.debug("Profile created", uri);
          let profileList: any = this.$refs.profileList;
          profileList.refresh();
        })
    );
  }

  callUpdateProfileService(form: ProfileUpdateDTO, done) {
    done(
      this.service
        .updateProfile(this.user.getAuthorizationHeader(), form)
        .then((http: HttpResponse<OpenSilexResponse<any>>) => {
          let uri = http.response.result;
          console.debug("Profile updated", uri);
          let profileList: any = this.$refs.profileList;
          profileList.refresh();
        })
    );
  }

  editProfile(form: ProfileGetDTO) {
    let profileForm: any = this.$refs.profileForm;
    profileForm.showEditForm(form);
  }

  deleteProfile(uri: string) {
    this.service
      .deleteProfile(this.user.getAuthorizationHeader(), uri)
      .then(() => {
        let profileList: any = this.$refs.profileList;
        profileList.refresh();
      })
      .catch(this.$opensilex.errorHandler);
  }
}
</script>

<style scoped lang="scss">
</style>
